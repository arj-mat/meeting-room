package com.santander.meeting.meetingroom.db.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.santander.meeting.meetingroom.AppUtils;
import com.santander.meeting.meetingroom.enums.RoomMediaType;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Entity
@Table(name = "room_media")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomMediaEntity {
    @Id
    @Column(columnDefinition = "CHAR(36)")
    @Builder.Default
    private String id = AppUtils.randomUUID();

    @Column(name = "author_member_id", columnDefinition = "CHAR(16)")
    @NotNull
    @NotEmpty
    private String authorMemberId;

    @Column(name = "room_id", columnDefinition = "CHAR(36)")
    @NotNull
    @NotEmpty
    private String roomId;

    @NotNull
    @Builder.Default
    private OffsetDateTime date = LocalDateTime.now().atOffset( ZoneOffset.UTC );

    @NotNull
    @NotEmpty
    @Column(name = "readable_length", columnDefinition = "TEXT")
    private String readableLength;

    @NotNull
    @NotEmpty
    @Column(columnDefinition = "CHAR(64)")
    @JsonIgnore
    private String hash;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "TEXT")
    private RoomMediaType type;
}
