package com.santander.meeting.meetingroom.oauth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.santander.meeting.meetingroom.external.DiscordAccessRequest;
import com.santander.meeting.meetingroom.external.DiscordUserRequest;
import com.santander.meeting.meetingroom.external.model.DiscordAccessData;
import com.santander.meeting.meetingroom.external.model.DiscordUserData;

import java.util.Optional;

public class DiscordOAuth {
    public static Optional<DiscordAccessData> exchangeCodeForAccessToken(String accessCode) {
        try {
            return new DiscordAccessRequest().execute( accessCode ).data;
        } catch ( JsonProcessingException e ) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public static Optional<DiscordUserData> getCurrentUser(String accessCode) {
        Optional<DiscordAccessData> discordAccessData = Optional.empty();

        try {
            discordAccessData = new DiscordAccessRequest().execute( accessCode ).data;
        } catch ( JsonProcessingException e ) {
            e.printStackTrace();
        }

        if ( discordAccessData.isPresent() ) {
            return new DiscordUserRequest().execute( discordAccessData.get().getAccess_token() ).data;
        } else {
            return Optional.empty();
        }
    }
}
