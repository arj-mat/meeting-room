package com.santander.meeting.meetingroom.db.service;

import com.google.common.hash.Hashing;
import com.santander.meeting.meetingroom.AppUtils;
import com.santander.meeting.meetingroom.dto.response.RoomJoinResponseDTO;
import com.santander.meeting.meetingroom.db.entity.MemberAccessEntity;
import com.santander.meeting.meetingroom.db.entity.RoomEntity;
import com.santander.meeting.meetingroom.db.entity.RoomMemberEntity;
import com.santander.meeting.meetingroom.db.repository.MemberAccessRepository;
import com.santander.meeting.meetingroom.db.repository.RoomMemberRepository;
import com.santander.meeting.meetingroom.db.repository.RoomRepository;
import com.santander.meeting.meetingroom.external.model.DiscordUserData;
import com.santander.meeting.meetingroom.oauth.DiscordOAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Service
public class RoomJoiningDBService {
    @Autowired
    private RoomRepository repository;

    @Autowired
    private RoomDBService roomDBService;

    @Autowired
    private RoomMemberRepository memberRepository;

    @Autowired
    private MemberAccessRepository accessRepository;

    public static String generateMemberAccessCode(String memberId, RoomEntity room) {
        return Hashing.sha512().hashString(
                String.format(
                        "%s-%s",
                        memberId,
                        room.getSecretSalt()
                ),
                StandardCharsets.UTF_8
        ).toString();
    }

    public Optional<RoomJoinResponseDTO> rejoinRoomWithAccessCode(RoomEntity room, String accessCode) {
        Optional<RoomMemberEntity> existingMemberRegistry = this.roomDBService.findMemberByAccessCode( accessCode );

        if ( existingMemberRegistry.isPresent() ) {
            this.memberRepository.save(
                    existingMemberRegistry.get()
                                          .setIsInRoom( true )
            );

            return Optional.of( RoomJoinResponseDTO.builder()
                                                   .accessCode( accessCode )
                                                   .memberId( existingMemberRegistry.get().getId() )
                                                   .roomId( room.getId() )
                                                   .roomName( room.getName() )
                                                   .build()
            );
        } else {
            return Optional.empty();
        }
    }

    public RoomJoinResponseDTO joinRoomWithNameAndEmail(RoomEntity room, String name, String email) {
        RoomMemberEntity addedMember = this.roomDBService.saveNewMember(
                room,
                name,
                Optional.empty(),
                Optional.empty()
        );

        final String memberAccessCode = RoomJoiningDBService.generateMemberAccessCode( addedMember.getId(), room );

        this.accessRepository.save(
                MemberAccessEntity
                        .builder()
                        .roomMember( addedMember )
                        .memberId( addedMember.getId() )
                        .code( memberAccessCode )
                        .build()
        );

        return RoomJoinResponseDTO.builder()
                                  .accessCode( memberAccessCode )
                                  .memberId( addedMember.getId() )
                                  .roomId( room.getId() )
                                  .roomName( room.getName() )
                                  .build();

    }


    public RoomJoinResponseDTO joinRoomFromDiscord(RoomEntity room, String token) {
        Optional<DiscordUserData> discordUser = DiscordOAuth.getCurrentUser( token );

        if ( discordUser.isPresent() ) {
            // Obtém uma RoomMemberEntity pelo pelo ID do Discord ou cria uma nova:
            RoomMemberEntity memberEntity = this.memberRepository
                    .findByDiscordUserId( discordUser.get().getId(), room.getId() )
                    .orElseGet( () -> this.roomDBService.saveNewMember(
                            room,
                            discordUser.get().getUsername(),
                            Optional.of(
                                    String.format(
                                            "https://cdn.discordapp.com/avatars/%s/%s.png?size=128",
                                            discordUser.get().getId(),
                                            discordUser.get().getAvatar()
                                    )
                            ),
                            Optional.of( discordUser.get().getId() )
                    ) );

            final String memberAccessCode = RoomJoiningDBService.generateMemberAccessCode( memberEntity.getId(), room );

            this.accessRepository.save(
                    MemberAccessEntity
                            .builder()
                            .roomMember( memberEntity )
                            .memberId( memberEntity.getId() )
                            .code( memberAccessCode )
                            .build()
            );

            return RoomJoinResponseDTO.builder()
                                      .accessCode( memberAccessCode )
                                      .memberId( memberEntity.getId() )
                                      .roomId( room.getId() )
                                      .roomName( room.getName() )
                                      .build();
        }

        return null;
    }
}
