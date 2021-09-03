package com.santander.meeting.meetingroom;

public class AppConfig {
    public static final String DISCORD_OAUTH_CLIENT_ID = "882309719367249981";
    public static final String DISCORD_OAUTH_REDIRECT_URI = System.getenv( "DISCORD_OAUTH_URI" );
    public static final String DISCORD_OAUTH_CLIENT_SECRET = System.getenv( "DISCORD_API_SECRET" );
}
