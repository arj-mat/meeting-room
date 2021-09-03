package com.santander.meeting.meetingroom.db.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.santander.meeting.meetingroom.AppUtils;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Random;

@Entity
@Table(name = "room")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomEntity {
    @Id
    @Column(columnDefinition = "CHAR(36)")
    @Builder.Default
    private String id = AppUtils.randomUUID();

    @NotNull
    @Column(name = "created_at")
    @Builder.Default
    private OffsetDateTime date = LocalDateTime.now().atOffset( ZoneOffset.UTC );

    @NotNull
    @NotEmpty
    @Column(columnDefinition = "VARCHAR(255)")
    private String name;

    @NotNull
    @NotEmpty
    @Column(name = "secret_salt", columnDefinition = "TEXT")
    @Builder.Default
    @JsonIgnore // Esta anotação impede que este campo seja incluído em uma resposta Http
    private String secretSalt = AppUtils.randomLowerCaseAlphanumericStr(
            new Random().ints( 1, 32, 64 ).findFirst().orElse( 32 )
    );
}
