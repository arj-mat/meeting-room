package com.santander.meeting.meetingroom.messaging.message.input;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ChatMessageInput {
    public String content;
}
