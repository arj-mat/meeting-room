package com.santander.meeting.meetingroom.db.repository;

import com.santander.meeting.meetingroom.db.entity.MemberAccessEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberAccessRepository extends JpaRepository<MemberAccessEntity, String> {
    Optional<MemberAccessEntity> findByCode(String code);
}
