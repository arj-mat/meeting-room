package com.santander.meeting.meetingroom.db.repository;

import com.santander.meeting.meetingroom.db.entity.RoomMediaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomMediaRepository extends JpaRepository<RoomMediaEntity, String> {
    List<RoomMediaEntity> findAllByRoomId(String roomId);
}
