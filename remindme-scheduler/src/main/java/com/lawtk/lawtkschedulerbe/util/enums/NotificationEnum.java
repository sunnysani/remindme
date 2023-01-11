package com.lawtk.lawtkschedulerbe.util.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NotificationEnum {
    TYPE_SCHEDULE("schedule"),
    TYPE_REMINDER("reminder");

    private String type;
}
