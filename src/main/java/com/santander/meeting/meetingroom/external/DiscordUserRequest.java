package com.santander.meeting.meetingroom.external;

import com.santander.meeting.meetingroom.external.model.DiscordUserData;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;

public class DiscordUserRequest extends ExternalRequest<DiscordUserData> {

    public DiscordUserRequest() {
        super( DiscordUserData::new );
    }

    public ExternalResult<DiscordUserData> execute(String accessToken) {
        return this._execute(
                HttpRequest.newBuilder()
                           .version( HttpClient.Version.HTTP_1_1 )
                           .GET()
                           .setHeader( "authorization", String.format( "Bearer %s", accessToken ) )
                           .setHeader( "accepts", "application/json" )
                           .uri( URI.create( "https://discord.com/api/v9/users/@me" ) )
                           .build()
        );
    }
}
