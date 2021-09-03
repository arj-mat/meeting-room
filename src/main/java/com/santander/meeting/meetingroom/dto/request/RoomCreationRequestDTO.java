package com.santander.meeting.meetingroom.dto.request;

import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomCreationRequestDTO {
    @NotNull
    @Size(min = 3, max = 50)
    private String name;
}
