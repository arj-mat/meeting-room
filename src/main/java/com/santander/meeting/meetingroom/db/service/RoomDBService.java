package com.santander.meeting.meetingroom.db.service;

import com.santander.meeting.meetingroom.dto.response.RoomCreationResponseDTO;
import com.santander.meeting.meetingroom.dto.response.RoomInfoDTO;
import com.santander.meeting.meetingroom.db.entity.MemberAccessEntity;
import com.santander.meeting.meetingroom.db.entity.RoomChatMessageEntity;
import com.santander.meeting.meetingroom.db.entity.RoomEntity;
import com.santander.meeting.meetingroom.db.entity.RoomMemberEntity;
import com.santander.meeting.meetingroom.db.repository.MemberAccessRepository;
import com.santander.meeting.meetingroom.db.repository.RoomChatMessageRepository;
import com.santander.meeting.meetingroom.db.repository.RoomMemberRepository;
import com.santander.meeting.meetingroom.db.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RoomDBService {
    @Autowired
    private RoomRepository repository;

    @Autowired
    private MemberAccessRepository accessRepository;

    @Autowired
    private RoomMemberRepository memberRepository;

    @Autowired
    private RoomChatMessageRepository messageRepository;

    public Optional<RoomEntity> findById(String id) {
        return this.repository.findById( id );
    }

    public Optional<RoomMemberEntity> findMemberByAccessCode(String accessCode) {
        return this.accessRepository.findByCode( accessCode ).map( MemberAccessEntity::getRoomMember );
    }

    public RoomMemberEntity saveNewMember(RoomEntity targetRoom, String displayName, Optional<String> avatarURL,
                                          Optional<String> discordUserId) {
        RoomMemberEntity member = RoomMemberEntity.builder()
                                                  .roomId( targetRoom.getId() )
                                                  .displayName( displayName )
                                                  .avatarURL( avatarURL.orElse( null ) )
                                                  .discordUserId( discordUserId.orElse( null ) )
                                                  .isAdmin( this.memberRepository.countByRoomId( targetRoom.getId() ) == 0 )
                                                  .build();

        return this.memberRepository.save( member );
    }

    public RoomInfoDTO getRoomInfoDTO(RoomEntity room) {
        return RoomInfoDTO
                .builder()
                .id( room.getId() )
                .name( room.getName() )
                .chatMessages(
                        this.messageRepository.findAllByRoomIdLimitingResults( room.getId(), 100 )
                )
                .memberList( this.memberRepository.findAllByRoomId( room.getId() ) )
                .build();
    }

    public RoomCreationResponseDTO createRoom(String name) {
        RoomEntity entity = this.repository.save(
                RoomEntity.builder()
                          .name( name )
                          .build()
        );

        return RoomCreationResponseDTO.builder()
                .success( true )
                .roomId( Optional.of( entity.getId() ) )
                .build();
    }

    public RoomChatMessageEntity saveRoomChatMessage(String roomId, String authorMemberId, String messageId, String content) {
        return this.messageRepository.save(
                RoomChatMessageEntity.builder()
                                     .id( messageId )
                                     .roomId( roomId )
                                     .authorMemberId( authorMemberId )
                                     .content( content )
                                     .build()
        );
    }
}
