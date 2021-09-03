package com.santander.meeting.meetingroom.dto.response;

import com.santander.meeting.meetingroom.db.entity.RoomChatMessageEntity;
import com.santander.meeting.meetingroom.db.entity.RoomMemberEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;
import java.util.List;

@Getter
@Setter
@Builder
public class RoomInfoDTO {
    private String id;
    private String name;
    private List<RoomMemberEntity> memberList;
    private Collection<RoomChatMessageEntity> chatMessages;
}
