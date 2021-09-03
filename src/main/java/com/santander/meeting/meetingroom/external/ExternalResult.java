package com.santander.meeting.meetingroom.external;

import java.util.Optional;

public class ExternalResult<T> {
    public int status;
    public Optional<T> data = Optional.empty();

    public ExternalResult(int status) {
        this.status = status;
    }
}
