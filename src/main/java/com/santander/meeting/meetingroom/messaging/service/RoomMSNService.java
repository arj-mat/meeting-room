package com.santander.meeting.meetingroom.messaging.service;

import com.google.common.hash.Hashing;
import com.santander.meeting.meetingroom.db.entity.RoomEntity;
import com.santander.meeting.meetingroom.db.entity.RoomMediaEntity;
import com.santander.meeting.meetingroom.db.entity.RoomMemberEntity;
import com.santander.meeting.meetingroom.db.repository.MemberAccessRepository;
import com.santander.meeting.meetingroom.db.repository.RoomChatMessageRepository;
import com.santander.meeting.meetingroom.db.repository.RoomMediaRepository;
import com.santander.meeting.meetingroom.db.repository.RoomMemberRepository;
import com.santander.meeting.meetingroom.db.service.RoomDBService;
import com.santander.meeting.meetingroom.enums.RoomMediaType;
import com.santander.meeting.meetingroom.messaging.MSNConnection;
import com.santander.meeting.meetingroom.messaging.MSNMemberIdentification;
import com.santander.meeting.meetingroom.messaging.message.output.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RoomMSNService {
    private SimpMessagingTemplate template;

    // https://stackoverflow.com/questions/22367223/sending-message-to-specific-user-on-spring-websocket
    // https://huongdanjava.com/send-stomp-message-to-a-specific-user-with-spring-websocket.html

    @Autowired
    public RoomMSNService(SimpMessagingTemplate template) {
        this.template = template;
    }

    @Autowired
    private MSNConnectionService connectionService;

    @Autowired
    private RoomDBService roomDBService;

    @Autowired
    private RoomMediaRepository mediaRepository;

    @Autowired
    private RoomMemberRepository memberRepository;

    @Autowired
    private MemberAccessRepository accessRepository;

    @Autowired
    private RoomChatMessageRepository messageRepository;

    public HashMap<String, RoomMediaSyncAvailabilityRequest> mediaSyncAvailabilityRequests = new HashMap<>();

    public RoomMediaSyncAvailabilityRequest createRoomMediaSyncAvailabilityRequest(String roomId, String mediaId,
                                                                                   String requestingMemberId) {
        var request = RoomMediaSyncAvailabilityRequest
                .builder()
                .mediaId( mediaId )
                .requestingMemberId( requestingMemberId )
                .roomId( roomId )
                .answersNeeded( MSNConnectionService.connectionsByRoomId.get( roomId ).size() )
                .build();

        // Todo: um mecanismo de timeout para essas requests

        this.mediaSyncAvailabilityRequests.put( request.getRequestId(), request );

        return request;
    }

    public void registerMediaSyncAvailableFromMember(MSNConnection memberConnection, String requestId) {
        var request = this.mediaSyncAvailabilityRequests.get( requestId );

        request.setIsAcceptingAnswers( false );
        request.setMemberIdToProvideData( memberConnection.getMemberIdentification().id );

        memberConnection.sendIfIdentified(
                "/media-sync-request",
                RoomMediaSyncRequest.builder()
                                    .requestId( requestId )
                                    .mediaId( request.getMediaId() )
                                    .build()
        );
    }

    public void finishMediaSyncDataNotAvailable(String requestId) {
        var request = this.mediaSyncAvailabilityRequests.get( requestId );

        if ( request == null ) {
            return;
        }

        Optional<MSNConnection> requestingMemberConnection = MSNConnectionService
                .connectionsByRoomId
                .get( ( request.getRoomId() ) )
                .stream()
                .filter( msnConnection -> msnConnection
                        .getMemberIdentification() != null && msnConnection
                        .getMemberIdentification().id == request
                        .getRequestingMemberId() )
                .findFirst();

        if ( requestingMemberConnection.isPresent() ) {
            requestingMemberConnection.get().sendIfIdentified(
                    "/media-sync-not-available",
                    RoomMediaSyncNotAvailable
                            .builder()
                            .mediaId( request.getMediaId() )
                            .build()
            );
        }

        this.mediaSyncAvailabilityRequests.remove( requestId );
    }

    public void registerMediaSyncNotAvailable(String requestId) {
        var request = this.mediaSyncAvailabilityRequests.get( requestId );

        request.incAnswersReceived();

        if ( request.getAnswersReceived() >= request.getAnswersNeeded() ) {
            this.finishMediaSyncDataNotAvailable( requestId );
        }
    }

    public void finishMediaSyncWithData(String requestId, MSNConnection sourceConnection, String data) {
        var request = this.mediaSyncAvailabilityRequests.get( requestId );

        if ( request == null || !request.getMemberIdToProvideData().equals( sourceConnection.getMemberIdentification().id ) ) {
            return;
        }

        RoomMediaEntity mediaEntity = this.mediaRepository.findById( request.getMediaId() ).orElse( null );

        if ( mediaEntity == null ) {
            this.finishMediaSyncDataNotAvailable( requestId );
            return;
        }

        String receivedDataHash = Hashing.sha256().hashString( data, StandardCharsets.UTF_8 ).toString();

        if ( !receivedDataHash.equals( mediaEntity.getHash() ) ) {
            this.mediaSyncAvailabilityRequests.remove( requestId );
            sourceConnection.disconnect();
            return;
        }

        Optional<MSNConnection> requestingMemberConnection = MSNConnectionService
                .connectionsByRoomId
                .get( ( request.getRoomId() ) )
                .stream()
                .filter( msnConnection -> msnConnection
                        .getMemberIdentification() != null && msnConnection
                        .getMemberIdentification().id == request
                        .getRequestingMemberId() )
                .findFirst();

        if ( requestingMemberConnection.isPresent() ) {
            requestingMemberConnection.get().sendIfIdentified(
                    "/media-sync-data",
                    RoomMediaSyncDataOutput.builder()
                                           .mediaId( request.getMediaId() )
                                           .data( data )
                                           .build()
            );
        }

        this.mediaSyncAvailabilityRequests.remove( requestId );
    }

    public RoomInfoMessageOutput getRoomInfoMessageOutput(RoomEntity room, MSNConnection connection) {
        Map<String, MSNMemberIdentification> onlineMembers = new HashMap<>();

        MSNConnectionService.connectionsByRoomId
                .get( room.getId() )
                .stream()
                .map( MSNConnection::getMemberIdentification )
                .collect( Collectors.toList() )
                .forEach( msnMemberIdentification -> onlineMembers.put( msnMemberIdentification.id, msnMemberIdentification ) );

        List<RoomMemberEntity> offlineMembers = this.memberRepository
                .findAllByRoomId( room.getId() )
                .stream()
                .filter( roomMemberEntity -> !onlineMembers.containsKey( roomMemberEntity.getId() ) )
                .collect( Collectors.toList() );

        return RoomInfoMessageOutput.builder()
                                    .me( connection.getMemberIdentification() )
                                    .roomName( room.getName() )
                                    .chatMessages( this.messageRepository.findAllByRoomIdLimitingResults( room.getId(), 100 ) )
                                    .onlineMembers( new ArrayList<>( onlineMembers.values() ) )
                                    .offlineMembers( offlineMembers )
                                    .media( this.mediaRepository.findAllByRoomId( connection.getMemberIdentification().roomId ) )
                                    .build();
    }

    public static void broadcastRoomChatMessage(String roomId, String authorMemberId, String messageId, String content) {
        ChatMessageOutput messageOutput = ChatMessageOutput
                .builder()
                .authorMemberId( authorMemberId )
                .id( messageId )
                .content( content )
                .build();

        MSNConnectionService.
                connectionsByRoomId
                .get( roomId )
                .forEach( msnConnection -> msnConnection.sendIfIdentified( "/chat-output", messageOutput ) );
    }

    public static void broadcastMemberJoined(MSNMemberIdentification member) {
        RoomMemberUpdateMessageOutput payload = RoomMemberUpdateMessageOutput
                .builder()
                .id( member.id )
                .displayName( member.displayName )
                .avatarURL( member.avatarURL )
                .isInRoom( true )
                .build();

        MSNConnectionService
                .connectionsByRoomId
                .get( member.roomId )
                .forEach( msnConnection -> msnConnection.sendIfIdentifiedAsDifferentMember( member.id,
                                                                                            "/member-update",
                                                                                            payload ) );
    }

    public static void sendToMembers(String roomId, String destination, Object payload) {
        if ( MSNConnectionService.connectionsByRoomId.containsKey( roomId ) ) {
            MSNConnectionService
                    .connectionsByRoomId
                    .get( roomId )
                    .forEach( msnConnection -> msnConnection.sendIfIdentified( destination, payload ) );
        }
    }

    public static void broadcastMemberLeft(MSNMemberIdentification member) {
        RoomMemberUpdateMessageOutput payload = RoomMemberUpdateMessageOutput
                .builder()
                .id( member.id )
                .displayName( member.displayName )
                .avatarURL( member.avatarURL )
                .isInRoom( false )
                .build();

        MSNConnectionService
                .connectionsByRoomId
                .get( member.roomId )
                .forEach( msnConnection -> msnConnection.sendIfIdentifiedAsDifferentMember( member.id,
                                                                                            "/member-update",
                                                                                            payload ) );
    }

    public void newAudioRecording(MSNMemberIdentification member, String data) {
        RoomMediaEntity entity = this.mediaRepository.save(
                RoomMediaEntity.builder()
                               .roomId( member.roomId )
                               .authorMemberId( member.id )
                               .type( RoomMediaType.RECORDED_AUDIO )
                               .hash( Hashing.sha256().hashString( data, StandardCharsets.UTF_8 ).toString() )
                               .build()
        );

        RoomMediaOutput mediaOutput = new RoomMediaOutput( entity, data );

        RoomMSNService.sendToMembers( member.roomId, "/new-media", mediaOutput );
    }
}
