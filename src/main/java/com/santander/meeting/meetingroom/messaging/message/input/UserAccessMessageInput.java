package com.santander.meeting.meetingroom.messaging.message.input;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserAccessMessageInput {
    public String roomId;
    public String memberId;
    public String accessCode;
}
