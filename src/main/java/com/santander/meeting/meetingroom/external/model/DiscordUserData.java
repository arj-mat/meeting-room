package com.santander.meeting.meetingroom.external.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DiscordUserData {
    private String id;
    private String username;
    private String discriminator;
    private String avatar;
    private Boolean verified;
    private String email;
    private Integer flags;
    private String banner;
    private Integer accent_color;
    private Integer premium_type;
    private Integer public_flags;
    private Integer banner_color;
    private String locale;
    private Boolean mfa_enabled;
}