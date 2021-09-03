package com.santander.meeting.meetingroom.controller;

import com.santander.meeting.meetingroom.dto.request.RoomCreationRequestDTO;
import com.santander.meeting.meetingroom.dto.request.RoomJoinWithEmailRequestDTO;
import com.santander.meeting.meetingroom.dto.response.RoomCreationResponseDTO;
import com.santander.meeting.meetingroom.dto.response.RoomJoinResponseDTO;
import com.santander.meeting.meetingroom.db.entity.RoomEntity;
import com.santander.meeting.meetingroom.db.service.RoomJoiningDBService;
import com.santander.meeting.meetingroom.db.service.RoomDBService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api")
public class APIHttpController {
    @Autowired
    private RoomJoiningDBService roomJoiningDBService;

    @Autowired
    private RoomDBService roomDBService;

    @PostMapping(value = "/join-room/{id}/with-email", consumes = MediaType.APPLICATION_JSON_VALUE, produces =
            MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RoomJoinResponseDTO> joinRoomWithEmailRequest(@PathVariable("id") String roomId,
                                                                        @Valid @RequestBody RoomJoinWithEmailRequestDTO request) {
        Optional<RoomEntity> room = this.roomDBService.findById( roomId );

        if ( room.isPresent() ) {
            return ResponseEntity.ok(
                    this.roomJoiningDBService.joinRoomWithNameAndEmail(
                            room.get(),
                            request.getName(),
                            request.getEmail()
                    )
            );
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping(value = "/join-room/{id}/from-discord", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RoomJoinResponseDTO> joinRoomFromDiscordRequest(@PathVariable("id") String roomId,
                                                                          @RequestParam("token") String token) {
        Optional<RoomEntity> room = this.roomDBService.findById( roomId );

        if ( room.isPresent() ) {
            return ResponseEntity.ok(
                    this.roomJoiningDBService.joinRoomFromDiscord( room.get(), token )
            );
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping(value = "/new-room", consumes = MediaType.APPLICATION_JSON_VALUE,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RoomCreationResponseDTO> getRoom(@Valid @RequestBody RoomCreationRequestDTO request) {
        return ResponseEntity.ok(this.roomDBService.createRoom(request.getName()) );
    }
}
