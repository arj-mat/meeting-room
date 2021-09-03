package com.santander.meeting.meetingroom.db.repository;


import com.santander.meeting.meetingroom.db.entity.RoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<RoomEntity, String> {
}
