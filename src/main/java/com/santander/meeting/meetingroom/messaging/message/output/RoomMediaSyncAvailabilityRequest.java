package com.santander.meeting.meetingroom.messaging.message.output;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.santander.meeting.meetingroom.AppUtils;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RoomMediaSyncAvailabilityRequest {
    @Builder.Default
    private String requestId = AppUtils.randomSmallUUID();
    private String mediaId;

    @JsonIgnore
    private String roomId;

    @JsonIgnore
    private String requestingMemberId;

    @JsonIgnore
    private Integer answersNeeded;

    @Builder.Default
    @JsonIgnore
    private Integer answersReceived = 0;

    @Builder.Default
    @JsonIgnore
    private Boolean isAcceptingAnswers = true;

    @JsonIgnore
    private String memberIdToProvideData;

    public Integer incAnswersReceived() {
        this.answersReceived++;
        return this.answersReceived;
    }
}