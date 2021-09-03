package com.santander.meeting.meetingroom.messaging.message.output;

import com.santander.meeting.meetingroom.db.entity.RoomChatMessageEntity;
import com.santander.meeting.meetingroom.db.entity.RoomMediaEntity;
import com.santander.meeting.meetingroom.db.entity.RoomMemberEntity;
import com.santander.meeting.meetingroom.messaging.MSNMemberIdentification;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;
import java.util.List;

@Getter
@Setter
@Builder
public class RoomInfoMessageOutput {
    private MSNMemberIdentification me;
    private String roomName;
    private List<RoomMemberEntity> offlineMembers;
    private List<MSNMemberIdentification> onlineMembers;
    private Collection<RoomChatMessageEntity> chatMessages;
    private List<RoomMediaEntity> media;
}