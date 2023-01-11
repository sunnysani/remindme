package com.lawtk.lawtkschedulerbe.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.Instant;

@Data
@RequiredArgsConstructor
public class NotificationDto {
    private final String type = "Schedule";

    @JsonProperty(value = "is_read", required = true)
    private final boolean isRead = false;

    @JsonProperty(value = "notification_time", required = true)
    private final String notificationTime = Instant.now().toString();

    private final String name;
}
