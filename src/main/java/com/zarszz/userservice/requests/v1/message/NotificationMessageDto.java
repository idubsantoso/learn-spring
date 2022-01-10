package com.zarszz.userservice.requests.v1.message;

import java.util.Map;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor 
public class NotificationMessageDto {
    private String message;

    @JsonProperty("app_id")
    private String appId;

    @JsonProperty("included_segments")
    private String[] includedSegments;

    private Map<String, String> data;
    private Map<String, String> contents;
}
