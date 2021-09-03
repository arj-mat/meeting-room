package com.santander.meeting.meetingroom.messaging.message.output;

import com.santander.meeting.meetingroom.AppUtils;
import com.santander.meeting.meetingroom.db.entity.RoomMediaEntity;
import com.santander.meeting.meetingroom.enums.RoomMediaType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoomMediaOutput extends RoomMediaEntity {
    private String data;

    public RoomMediaOutput(RoomMediaEntity entity, String data) {
        this.data = data;
        this.setId( entity.getId() );
        this.setDate( entity.getDate() );
        this.setAuthorMemberId( entity.getAuthorMemberId() );
        this.setHash( entity.getHash() );
        this.setType( entity.getType() );
        this.setReadableLength( entity.getReadableLength() );
    }
}
