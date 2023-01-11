package com.lawtk.lawtkschedulerbe.controller;

import com.lawtk.lawtkschedulerbe.entity.JobInfo;
import com.lawtk.lawtkschedulerbe.service.JobInfoService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "api/job")
public class JobController {
    private final JobInfoService schedulerService;

    @Autowired
    public JobController(JobInfoService schedulerService){
        this.schedulerService = schedulerService;
    }

    @PostMapping(value = "/save")
    public JobInfo addNewJob(@RequestBody JobInfo jobInfo){
        return schedulerService.saveOrUpdate(jobInfo);
    }

    @GetMapping("/getAllJobs")
    public Object getAllJobs() throws SchedulerException {
        List<JobInfo> jobList = schedulerService.getAllJobList();
        return jobList;
    }
}
