package com.santander.meeting.meetingroom.messaging.message.input;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RoomMediaSyncAvailabilityAnswer {
    private String requestId;
    private Boolean isAvailable;
}