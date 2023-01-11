package com.lawtk.lawtkschedulerbe.util.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ScheduleEnum {
    INTERVAL_MINUTES("minutes"),
    INTERVAL_HOURS("hours"),
    INTERVAL_DAYS("days"),
    INTERVAL_WEEKS("weeks"),
    INTERVAL_MONTHS("months");

    private String interval;
}
