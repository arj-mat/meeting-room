package com.santander.meeting.meetingroom.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.util.Optional;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomCreationResponseDTO {
    private Boolean success;
    private Optional<String> error;
    private Optional<String> roomId;
}
