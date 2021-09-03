package com.santander.meeting.meetingroom.external;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.santander.meeting.meetingroom.AppConfig;
import com.santander.meeting.meetingroom.external.model.DiscordAccessData;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.util.HashMap;
import java.util.Map;

public class DiscordAccessRequest extends ExternalRequest<DiscordAccessData> {
    public DiscordAccessRequest() {
        super( DiscordAccessData::new );
    }

    public ExternalResult<DiscordAccessData> execute(String accessCode) throws JsonProcessingException {
        Map<String, String> data = new HashMap<>();

        data.put( "client_id", AppConfig.DISCORD_OAUTH_CLIENT_ID );
        data.put( "client_secret", AppConfig.DISCORD_OAUTH_CLIENT_SECRET );
        data.put( "redirect_uri", AppConfig.DISCORD_OAUTH_REDIRECT_URI );
        data.put( "grant_type", "authorization_code" );
        data.put( "code", accessCode );

        return this._execute(
                HttpRequest.newBuilder()
                           .version( HttpClient.Version.HTTP_1_1 )
                           .POST( this.mapToFormData( data ) )
                           .setHeader( "content-type", "application/x-www-form-urlencoded" )
                           .uri( URI.create( "https://discord.com/api/v9/oauth2/token" ) )
                           .build()
        );
    }
}
