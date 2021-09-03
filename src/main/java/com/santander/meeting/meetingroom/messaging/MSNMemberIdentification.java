package com.santander.meeting.meetingroom.messaging;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;

@Builder
public class MSNMemberIdentification {
    @JsonIgnore
    public String roomId;

    public String id;
    public String displayName;
    public String avatarURL;
}
