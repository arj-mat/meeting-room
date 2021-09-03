package com.santander.meeting.meetingroom.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RoomJoinResponseDTO {
    private String memberId;
    private String accessCode;
    private String roomId;
    private String roomName;
}
