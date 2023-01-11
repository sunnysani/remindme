package com.lawtk.lawtkschedulerbe.service;

import com.lawtk.lawtkschedulerbe.entity.JobInfo;
import com.lawtk.lawtkschedulerbe.quartz.component.JobScheduleCreator;
import com.lawtk.lawtkschedulerbe.quartz.job.SampleCronJob;
import com.lawtk.lawtkschedulerbe.quartz.job.SimpleJob;
import com.lawtk.lawtkschedulerbe.repository.JobInfoRepository;
import com.lawtk.lawtkschedulerbe.util.EntityUtils;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
@Transactional
public class JobInfoServiceImpl implements JobInfoService {
    @Autowired
    private Scheduler scheduler;

    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;

    @Autowired
    private JobInfoRepository jobInfoRepository;

    @Autowired
    private ApplicationContext context;

    @Autowired
    private JobScheduleCreator scheduleCreator;

    public SchedulerMetaData getMetaData() throws SchedulerException {
        SchedulerMetaData metaData = scheduler.getMetaData();
        return metaData;
    }

    public List<JobInfo> getAllJobList() {
        return jobInfoRepository.findAll();
    }

    public JobInfo saveOrUpdate(JobInfo jobInfo){
        if (jobInfo.getCronExpression().length() > 0) {
            jobInfo.setJobClass(SampleCronJob.class.getName());
            jobInfo.setCronJob(true);
        } else {
            jobInfo.setJobClass(SimpleJob.class.getName());
            jobInfo.setCronJob(false);
        }
        if (jobInfo.getId() != null) {
            log.info("Job Info: {}", jobInfo);
            scheduleNewJob(jobInfo);
        } else {
            updateScheduleJob(jobInfo);
        }
        jobInfo.setDescription("i am job number " + jobInfo.getId());
        log.info(">>>>> jobName = [" + jobInfo.getJobName() + jobInfo.getScheduleId().toString()+ "]" + " created.");
        return jobInfo;
    }

    public void scheduleNewJob(JobInfo jobInfo) {
        try {
            Scheduler scheduler = schedulerFactoryBean.getScheduler();

            JobDetail jobDetail = JobBuilder
                    .newJob((Class<? extends QuartzJobBean>) Class.forName(jobInfo.getJobClass()))
                    .withIdentity(jobInfo.getJobName() + jobInfo.getScheduleId().toString(), jobInfo.getJobGroup()).build();
            if (!scheduler.checkExists(jobDetail.getKey())) {
                jobDetail = scheduleCreator.createJob(
                        (Class<? extends QuartzJobBean>) Class.forName(jobInfo.getJobClass()), false, context,
                        jobInfo.getJobName() + jobInfo.getScheduleId().toString(), jobInfo.getJobGroup());

                Trigger trigger = scheduleCreator.createCronTrigger(jobInfo.getJobName() + jobInfo.getScheduleId().toString(), new Date(),
                        jobInfo.getCronExpression(), SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW, jobInfo);

                scheduler.scheduleJob(jobDetail, trigger);
                jobInfo.setJobStatus("SCHEDULED");
                jobInfoRepository.save(EntityUtils.persistable(jobInfo, true));
                log.info(">>>>> jobName = [" + jobInfo.getJobName() + jobInfo.getScheduleId().toString() + "]" + " scheduled.");
            } else {
                log.error("scheduleNewJobRequest.jobAlreadyExist");
            }
        } catch (ClassNotFoundException e) {
            log.error("Class Not Found - {}", jobInfo.getJobClass(), e);
        } catch (SchedulerException e) {
            log.error(e.getMessage(), e);
        }
    }

    public JobInfo updateScheduleJob(JobInfo jobInfo){
        Trigger newTrigger = scheduleCreator.createCronTrigger(jobInfo.getJobName() + jobInfo.getScheduleId().toString(),
                new Date(),
                jobInfo.getCronExpression(),
                SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW, jobInfo);
        try {
            schedulerFactoryBean.getScheduler().rescheduleJob(TriggerKey.triggerKey(jobInfo.getJobName() + jobInfo.getScheduleId().toString()), newTrigger);
            jobInfo.setJobStatus("EDITED & SCHEDULED");
            jobInfoRepository.save(jobInfo);
            log.info(">>>>> jobName = [" + jobInfo.getJobName() + jobInfo.getScheduleId().toString() + "]" + " updated and scheduled.");
        } catch (SchedulerException e) {
            log.error(e.getMessage(), e);
        }

        return jobInfo;
    }

    public void deleteJob(JobInfo jobInfo){
        try {
            log.info(">>>>> jobName = [" + jobInfo.getJobName() + jobInfo.getScheduleId().toString() + "]" + " deleted.");

            schedulerFactoryBean.getScheduler().deleteJob(new JobKey(jobInfo.getJobName() + jobInfo.getScheduleId().toString(), jobInfo.getJobGroup()));
            jobInfoRepository.delete(jobInfo);
        } catch (SchedulerException e) {
            log.error("Failed to delete job - {}", jobInfo.getJobName(), e);
        }
    }
}
