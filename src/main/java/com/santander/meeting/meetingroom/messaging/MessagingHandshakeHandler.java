package com.santander.meeting.meetingroom.messaging;

import com.santander.meeting.meetingroom.AppUtils;
import lombok.SneakyThrows;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.*;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.net.HttpCookie;
import java.util.Optional;
import java.util.stream.Collectors;

// Referência: https://stackoverflow.com/a/56531121

public class MessagingHandshakeHandler extends DefaultHandshakeHandler {
    @SneakyThrows
    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        final String name = AppUtils.randomUUID();
        return () -> name;
    }
}
