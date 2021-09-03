package com.santander.meeting.meetingroom.db.entity;

import com.santander.meeting.meetingroom.AppUtils;
import lombok.*;
import lombok.experimental.Accessors;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "room_member")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomMemberEntity {
    @Id
    @Column(columnDefinition = "CHAR(16)")
    @Builder.Default
    private String id = AppUtils.randomSmallUUID();

    @Column(name = "display_name", columnDefinition = "VARCHAR(50)")
    @Size(max = 50)
    @NotNull
    @NotEmpty
    @Accessors(chain = true)
    private String displayName;

    @Column(name = "avatar_url", columnDefinition = "TEXT")
    @Accessors(chain = true)
    private String avatarURL;

    @Column(name = "is_admin")
    @NotNull
    @Builder.Default
    private Boolean isAdmin = false;

    @Column(name = "is_in_room")
    @NotNull
    @Builder.Default
    @Accessors(chain = true)
    private Boolean isInRoom = true;

    @Column(name = "room_id", columnDefinition = "CHAR(36)")
    @NotNull
    @Getter(AccessLevel.PACKAGE)
    private String roomId;

    @Column(name = "discord_user_id", columnDefinition = "CHAR(18)")
    private String discordUserId;
}
