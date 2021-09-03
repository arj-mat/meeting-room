package com.santander.meeting.meetingroom.db.entity;

import com.santander.meeting.meetingroom.AppUtils;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

@Entity
@Table(name = "room_chat_message")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomChatMessageEntity {
    @Id
    @Column(columnDefinition = "CHAR(36)")
    @Builder.Default
    private String id = AppUtils.randomUUID();

    @Column(name = "author_member_id", columnDefinition = "CHAR(16)")
    @NotNull
    @NotEmpty
    private String authorMemberId;

    @Builder.Default
    private OffsetDateTime date = LocalDateTime.now().atOffset( ZoneOffset.UTC );

    @NotNull
    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "room_id", columnDefinition = "CHAR(36)")
    @NotNull
    @Getter(AccessLevel.PRIVATE)
    private String roomId;
}
