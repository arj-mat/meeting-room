package com.santander.meeting.meetingroom.messaging.message.output;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RoomMediaSyncDataOutput {
    private String mediaId;
    private String data;
}
