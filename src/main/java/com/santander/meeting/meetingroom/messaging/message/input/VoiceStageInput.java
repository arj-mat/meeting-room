package com.santander.meeting.meetingroom.messaging.message.input;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class VoiceStageInput {
    private String roomId;
    private Integer memberId;
    private String data;
}
