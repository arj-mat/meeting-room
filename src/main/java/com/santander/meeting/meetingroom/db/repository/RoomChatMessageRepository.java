package com.santander.meeting.meetingroom.db.repository;

import com.santander.meeting.meetingroom.db.entity.RoomChatMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;

public interface RoomChatMessageRepository extends JpaRepository<RoomChatMessageEntity, String> {
    @Query(value = "SELECT * FROM room_chat_message WHERE room_chat_message.room_id = :id ORDER BY room_chat_message.date DESC",
            nativeQuery = true)
    Collection<RoomChatMessageEntity> findAllByRoomId(@Param("id") String roomId);

    @Query(value = "SELECT * FROM room_chat_message WHERE room_chat_message.room_id = :id ORDER BY room_chat_message.date DESC LIMIT " +
            ":limit", nativeQuery = true)
    Collection<RoomChatMessageEntity> findAllByRoomIdLimitingResults(@Param("id") String roomId, @Param("limit") int limit);
}
