package com.lawtk.lawtkschedulerbe.service;

import com.lawtk.lawtkschedulerbe.entity.JobInfo;
import org.quartz.SchedulerException;
import org.quartz.SchedulerMetaData;

import java.util.List;

public interface JobInfoService {
    SchedulerMetaData getMetaData() throws SchedulerException;

    List<JobInfo> getAllJobList();

    JobInfo saveOrUpdate(JobInfo jobInfo);

    void scheduleNewJob(JobInfo jobInfo);

    JobInfo updateScheduleJob(JobInfo jobInfo);

    void deleteJob(JobInfo jobInfo);
}
