package com.santander.meeting.meetingroom.messaging.message.output;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Getter
@Setter
@Builder
public class ChatMessageOutput {
    private String id;
    private String authorMemberId;

    @Builder.Default
    private OffsetDateTime date = LocalDateTime.now().atOffset( ZoneOffset.UTC );

    private String content;
}