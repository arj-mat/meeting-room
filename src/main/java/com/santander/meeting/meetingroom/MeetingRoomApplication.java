package com.santander.meeting.meetingroom;

import com.fasterxml.jackson.databind.json.JsonMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDateTime;

@SpringBootApplication
public class MeetingRoomApplication {

    public static void main(String[] args) {
        SpringApplication.run( MeetingRoomApplication.class, args );

        System.out.println( "Servidor em execução." );
    }

}
