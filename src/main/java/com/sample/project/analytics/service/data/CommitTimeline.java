package com.sample.project.analytics.service.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.time.Instant;

/**
 * @author arvin on 8/15/20
 **/
@Value
@AllArgsConstructor
public class CommitTimeline {

    @JsonIgnore
    Instant timestamp;

    String message;

    String sha;

    @JsonProperty("timestamp")
    public String getTimestampStr() {
        return timestamp.toString();
    }
}
