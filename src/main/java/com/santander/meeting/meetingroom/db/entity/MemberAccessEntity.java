package com.santander.meeting.meetingroom.db.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "member_access")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberAccessEntity {
    @Id
    @Column(name = "member_id", columnDefinition = "CHAR(16)")
    private String memberId;

    @Column(columnDefinition = "CHAR(128)")
    private String code;

    @OneToOne(targetEntity = RoomMemberEntity.class, fetch = FetchType.EAGER)
    private RoomMemberEntity roomMember;
}
