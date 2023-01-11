package com.lawtk.lawtkschedulerbe.service;

import com.lawtk.lawtkschedulerbe.entity.Schedule;

import java.util.List;

public interface ScheduleService {
    Schedule addSchedule(Schedule schedule);

    Schedule retrieveScheduleById(Integer id);

    List<Schedule> retrieveAllSchedule(String username);

    Schedule updateSchedule(Schedule schedule);

    void deleteSchedule(Schedule schedule);
}
