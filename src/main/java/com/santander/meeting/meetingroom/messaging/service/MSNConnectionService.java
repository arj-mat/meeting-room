package com.santander.meeting.meetingroom.messaging.service;

import com.santander.meeting.meetingroom.db.entity.RoomEntity;
import com.santander.meeting.meetingroom.db.entity.RoomMemberEntity;
import com.santander.meeting.meetingroom.db.service.RoomDBService;
import com.santander.meeting.meetingroom.db.service.RoomJoiningDBService;
import com.santander.meeting.meetingroom.messaging.MSNConnection;
import com.santander.meeting.meetingroom.messaging.message.input.UserAccessMessageInput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
public class MSNConnectionService {
    public static HashMap<String, MSNConnection> connections = new HashMap<>();
    public static HashMap<String, List<MSNConnection>> connectionsByRoomId = new HashMap<>();

    @Autowired
    private RoomDBService roomDBService;

    public static SimpMessagingTemplate template;

    public static void sendUserIdentificationRequest(String connectionId) {
        MSNConnectionService.template.convertAndSendToUser( connectionId, "/identification-request", "" );
    }

    @Autowired
    public MSNConnectionService(SimpMessagingTemplate template) {
        MSNConnectionService.template = template;
    }

    public void sendToRoomConnections(String roomId, String destinationPrefix, Object payload) {
        MSNConnectionService.connectionsByRoomId
                .get( roomId )
                .forEach( msnConnection -> {
                              MSNConnectionService.template
                                      .convertAndSendToUser(
                                              msnConnection.getConnectionId(),
                                              destinationPrefix,
                                              payload
                                      );
                          }
                );
    }

    public static MSNConnection getFromPrincipal(Principal principal) {
        return MSNConnectionService.connections.get( principal.getName() );
    }

    public Optional<MSNConnection> getRoomMemberConnection(String roomId, String memberId) {
        return Optional.ofNullable( MSNConnectionService.connectionsByRoomId.get( roomId ) )
                       .orElse( new ArrayList<>() )
                       .stream()
                       .filter( memberConnection -> memberConnection.getMemberIdentification().id == memberId )
                       .findFirst();
    }

    public boolean identifyUser(MSNConnection connection, RoomEntity room, UserAccessMessageInput credentials) {
        if ( connection.getMemberIdentification() != null ) {
            return false;
        }

        Optional<RoomMemberEntity> memberEntity = this.roomDBService.findMemberByAccessCode( credentials.accessCode );

        if ( memberEntity.isPresent() ) {
            String expectedHash = RoomJoiningDBService.generateMemberAccessCode( credentials.memberId, room );

            if ( expectedHash.equals( credentials.accessCode ) ) {
                connection.setMemberIdentification( credentials.roomId, memberEntity.get() );

                return true;
            } else {
                connection.disconnect();

                return false;
            }
        } else {
            connection.disconnect();

            return false;
        }
    }
}
