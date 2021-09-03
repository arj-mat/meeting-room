package com.santander.meeting.meetingroom.db.repository;

import com.santander.meeting.meetingroom.db.entity.RoomMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RoomMemberRepository extends JpaRepository<RoomMemberEntity, String> {
    List<RoomMemberEntity> findAllByRoomId(String roomId);

    @Query(value = "SELECT * FROM room_member WHERE room_id = :id AND is_in_room = FALSE", nativeQuery = true)
    List<RoomMemberEntity> findAllOfflineByRoomId(@Param("id") String roomId);

    @Query(value = "SELECT * FROM room_member WHERE discord_user_id = :discord AND room_id = :room", nativeQuery = true)
    Optional<RoomMemberEntity> findByDiscordUserId(@Param("discord") String discordId, @Param("room") String roomId);

    long countByRoomId(String roomId);
}
