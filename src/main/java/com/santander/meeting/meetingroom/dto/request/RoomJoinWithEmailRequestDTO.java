package com.santander.meeting.meetingroom.dto.request;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import java.util.Optional;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomJoinWithEmailRequestDTO {
    @NotEmpty
    private String name;

    @NotEmpty
    private String email;

    private String accessCode;
}
