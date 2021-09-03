package com.santander.meeting.meetingroom.messaging.message.output;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RoomMemberUpdateMessageOutput {
    private String id;
    private String displayName;
    private String avatarURL;
    private Boolean isInRoom;
}
