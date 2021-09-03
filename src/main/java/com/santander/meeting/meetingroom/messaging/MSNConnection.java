package com.santander.meeting.meetingroom.messaging;

import com.santander.meeting.meetingroom.db.entity.RoomMemberEntity;
import com.santander.meeting.meetingroom.messaging.service.MSNConnectionService;
import com.santander.meeting.meetingroom.messaging.service.RoomMSNService;
import lombok.*;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Builder
@Getter
@Setter
public class MSNConnection {
    private String connectionId;

    @Setter(AccessLevel.NONE)
    @Builder.Default
    private MSNMemberIdentification memberIdentification = null;

    @Builder.Default
    private LocalDateTime connectedAt = LocalDateTime.now();

    @Builder.Default
    private LocalDateTime lastPing = LocalDateTime.now();

    private WebSocketSession session;

    @Getter(AccessLevel.PRIVATE)
    @Setter(AccessLevel.PRIVATE)
    private Timer connectionTimer;

    public void sendIfIdentified(String destination, Object payload) {
        if ( this.memberIdentification == null ) {
            return;
        }

        MSNConnectionService.template.convertAndSendToUser( this.connectionId, destination, payload );
    }

    public void sendIfIdentifiedAsDifferentMember(String memberIdToSkip, String destination, Object payload) {
        if ( this.memberIdentification == null || this.memberIdentification.id.equals( memberIdToSkip ) ) {
            return;
        }

        MSNConnectionService.template.convertAndSendToUser( this.connectionId, destination, payload );
    }

    public MSNConnection setup() {
        MSNConnection self = this;

        this.connectionTimer = new Timer();
        this.connectionTimer.scheduleAtFixedRate( new TimerTask() {
            @SneakyThrows
            public void run() {
                if ( self.session != null ) {
                    if ( self.memberIdentification == null ) {
                        MSNConnectionService.sendUserIdentificationRequest( self.connectionId );

                        if ( self.connectedAt.isBefore( LocalDateTime.now().minusSeconds( 30 ) ) ) {
                            // Desconectar caso o usuário ainda não tenha se identificado em 30 segundos
                            System.out.println( "Not yet identified " + self.connectionId );

                            self.session.close();
                        }

                    } else if ( self.lastPing.isBefore( LocalDateTime.now().minusSeconds( 60 ) ) ) {
                        // Desconectar se o último ping foi recebido há mais de 60 segundos
                        System.out.println( "Disconnected due to ping " + self.memberIdentification.displayName );

                        self.session.close();
                    }
                }
            }
        }, 0, 1000 );

        return this;
    }

    public void setMemberIdentification(String roomId, RoomMemberEntity memberEntity) {
        this.memberIdentification = MSNMemberIdentification
                .builder()
                .roomId( roomId )
                .id( memberEntity.getId() )
                .avatarURL( memberEntity.getAvatarURL() )
                .displayName( memberEntity.getDisplayName() )
                .build();

        if ( MSNConnectionService.connectionsByRoomId.containsKey( roomId ) ) {
            MSNConnectionService.connectionsByRoomId.get( roomId ).add( this );
        } else {
            MSNConnectionService.connectionsByRoomId.put( roomId, new ArrayList<>( Arrays.asList( this ) ) );
        }

        RoomMSNService.broadcastMemberJoined( this.memberIdentification );
    }

    public void disconnect() {
        if ( this.session != null && this.session.isOpen() ) {
            try {
                this.session.close();
            } catch ( IOException e ) {
                e.printStackTrace();
            }
        }

        if ( this.connectionTimer != null ) {
            this.connectionTimer.cancel();
        }

        if ( this.memberIdentification != null && MSNConnectionService.connectionsByRoomId.containsKey( this.memberIdentification.roomId ) ) {
            MSNConnectionService.connectionsByRoomId.get( this.memberIdentification.roomId ).remove( this );

            RoomMSNService.broadcastMemberLeft( this.memberIdentification );
        }

        if ( MSNConnectionService.connections.containsKey( this.connectionId ) ) {
            MSNConnectionService.connections.remove( this.connectionId );
        }
    }
}
