package com.santander.meeting.meetingroom.controller;

import com.santander.meeting.meetingroom.AppUtils;
import com.santander.meeting.meetingroom.db.entity.RoomEntity;
import com.santander.meeting.meetingroom.db.service.RoomDBService;
import com.santander.meeting.meetingroom.messaging.MSNConnection;
import com.santander.meeting.meetingroom.messaging.MSNMemberIdentification;
import com.santander.meeting.meetingroom.messaging.message.input.RoomMediaSyncAvailabilityAnswer;
import com.santander.meeting.meetingroom.messaging.message.input.RoomMediaSyncRequestAnswer;
import com.santander.meeting.meetingroom.messaging.message.input.UserAccessMessageInput;
import com.santander.meeting.meetingroom.messaging.service.MSNConnectionService;
import com.santander.meeting.meetingroom.messaging.service.RoomMSNService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Optional;

@Controller
public class MessagingController {
    @Autowired
    private RoomDBService roomDBService;

    @Autowired
    private RoomMSNService roomMSNService;

    @Autowired
    private MSNConnectionService msnConnectionService;

    private void attemptToAccessNonExistingRoom() {
        System.err.println( "attemptToAccessNonExistingRoom" );
    }

    @MessageMapping("/identify")
    private void userAccessMessageInput(Principal principal, @Payload UserAccessMessageInput payload) {
        Optional<RoomEntity> room = this.roomDBService.findById( payload.roomId );

        MSNConnection connection = MSNConnectionService.getFromPrincipal( principal );

        if ( room.isPresent() && this.msnConnectionService.identifyUser( connection, room.get(), payload ) ) {
            MSNConnectionService.template.convertAndSendToUser(
                    principal.getName(),
                    "/room-info",
                    this.roomMSNService.getRoomInfoMessageOutput( room.get(), connection )
            );
        } else {
            connection.disconnect();
        }
    }

    @MessageMapping("/ping")
    private void pingInput(Principal principal) {
        MSNConnection connection = MSNConnectionService.getFromPrincipal( principal );

        if ( connection.getMemberIdentification() != null ) {
            connection.setLastPing( LocalDateTime.now() );
        }
    }

    @MessageMapping("/chat")
    private void chatMessageInput(Principal principal, @Payload String content) {
        String messageId = AppUtils.randomUUID();

        MSNConnection connection = MSNConnectionService.getFromPrincipal( principal );

        MSNMemberIdentification member = connection.getMemberIdentification();

        RoomMSNService.broadcastRoomChatMessage( member.roomId, member.id, messageId, content );
        this.roomDBService.saveRoomChatMessage( member.roomId, member.id, messageId, content );
    }

    @MessageMapping("/audio-recording")
    private void audioRecordingInput(Principal principal, @Payload String data) {
        MSNConnection connection = MSNConnectionService.getFromPrincipal( principal );

        MSNMemberIdentification member = connection.getMemberIdentification();

        this.roomMSNService.newAudioRecording( member, data );

        connection.sendIfIdentified( "/audio-recording-sent", "{}" );
    }

    @MessageMapping("/request-media-sync-availability")
    private void mediaSyncAvailabilityRequest(Principal principal, @Payload String mediaId) {
        MSNConnection connection = MSNConnectionService.getFromPrincipal( principal );
        MSNMemberIdentification member = connection.getMemberIdentification();

        RoomMSNService.sendToMembers(
                connection.getMemberIdentification().roomId,
                "/media-sync-availability-request",
                this.roomMSNService.createRoomMediaSyncAvailabilityRequest( member.roomId, mediaId, member.id )
        );
    }

    @MessageMapping("/answer-media-sync-availability")
    private void mediaSyncAvailabilityAnswer(Principal principal, @Payload RoomMediaSyncAvailabilityAnswer payload) {
        var request = this.roomMSNService.mediaSyncAvailabilityRequests.get( payload.getRequestId() );

        if ( request == null || !request.getIsAcceptingAnswers() ) {
            return;
        }

        MSNConnection connection = MSNConnectionService.getFromPrincipal( principal );

        if ( payload.getIsAvailable() ) {
            this.roomMSNService.registerMediaSyncAvailableFromMember( connection, payload.getRequestId() );
        } else {
            this.roomMSNService.registerMediaSyncNotAvailable( payload.getRequestId() );
        }
    }

    @MessageMapping("/media-sync-data")
    private void mediaSyncRequestAnswer(Principal principal, @Payload RoomMediaSyncRequestAnswer payload) {
        MSNConnection connection = MSNConnectionService.getFromPrincipal( principal );

        this.roomMSNService.finishMediaSyncWithData( payload.getRequestId(), connection, payload.getData() );
    }
}
