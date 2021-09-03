package com.santander.meeting.meetingroom.messaging;

import com.santander.meeting.meetingroom.messaging.service.MSNConnectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;
import org.springframework.web.socket.handler.WebSocketHandlerDecoratorFactory;

// Referência: https://spring.io/guides/gs/messaging-stomp-websocket/

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker(
                "/chat",
                "/identification-request",
                "/room-info",
                "/member-update",
                "/audio-recording-sent",
                "/new-media",
                "/media-sync-availability-request",
                "/media-sync-request",
                "/media-sync-data",
                "/media-sync-not-available"
        );

        config.setUserDestinationPrefix( "/connections" );

        config.setApplicationDestinationPrefixes( "/inputs" );
    }

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
        registration.setMessageSizeLimit( 3000000 ); // 3 megabytes
        registration.setSendTimeLimit( 20 * 10000 ); // 20 segundos
        registration.setSendBufferSizeLimit( 3000000 ); // 3 megabytes


        // Referência: https://newbedev.com/disconnect-client-session-from-spring-websocket-stomp-server

        registration.addDecoratorFactory( new WebSocketHandlerDecoratorFactory() {
            @Override
            public WebSocketHandler decorate(final WebSocketHandler handler) {
                return new WebSocketHandlerDecorator( handler ) {
                    @Override
                    public void afterConnectionEstablished(final WebSocketSession session) throws Exception {
                        if ( session.getPrincipal() == null ) {
                            session.close();
                            return;
                        }

                        System.out.println( "connected " + session.getPrincipal().getName() );

                        MSNConnectionService.connections.put(
                                session.getPrincipal().getName(),
                                MSNConnection.builder()
                                             .connectionId( session.getPrincipal().getName() )
                                             .session( session )
                                             .build()
                                             .setup()
                        );

                        super.afterConnectionEstablished( session );
                    }

                    @Override
                    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
                        if ( session.getPrincipal() != null && MSNConnectionService.connections.containsKey( session.getPrincipal()
                                                                                                                    .getName() ) ) {
                            MSNConnectionService.connections.get( session.getPrincipal().getName() ).disconnect();
                        }
                    }
                };
            }
        } );
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint( "/gs-guide-websocket" )
                .setHandshakeHandler( new MessagingHandshakeHandler() )
                .setAllowedOriginPatterns( "*" )
                .withSockJS();
    }
}
