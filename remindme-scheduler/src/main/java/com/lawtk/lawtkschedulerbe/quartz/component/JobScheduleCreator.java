package com.lawtk.lawtkschedulerbe.quartz.component;

import java.text.ParseException;
import java.util.Date;

import com.lawtk.lawtkschedulerbe.entity.JobInfo;
import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.SimpleTrigger;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JobScheduleCreator {

    public JobDetail createJob(Class<? extends QuartzJobBean> jobClass, boolean isDurable,
                               ApplicationContext context, String jobName, String jobGroup) {
        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        factoryBean.setJobClass(jobClass);
        factoryBean.setDurability(isDurable);
        factoryBean.setApplicationContext(context);
        factoryBean.setName(jobName);
        factoryBean.setGroup(jobGroup);

        // set job data map
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put(jobName + jobGroup, jobClass.getName());
        factoryBean.setJobDataMap(jobDataMap);
        factoryBean.afterPropertiesSet();
        return factoryBean.getObject();
    }

    public CronTrigger createCronTrigger(String triggerName, Date startTime, String cronExpression, int misFireInstruction, JobInfo jobInfo) {
        CronTriggerFactoryBean factoryBean = new CronTriggerFactoryBean();
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("name", triggerName);
        jobDataMap.put("cron", cronExpression);
        jobDataMap.put("activity_name", jobInfo.getJobName());
        jobDataMap.put("schedule_time", jobInfo.getScheduleTime().toString());
        jobDataMap.put("schedule_interval", jobInfo.getScheduleInterval());
        jobDataMap.put("username", jobInfo.getUsername());

        factoryBean.setName(triggerName);
        factoryBean.setStartTime(startTime);
        factoryBean.setCronExpression(cronExpression);
        factoryBean.setMisfireInstruction(misFireInstruction);
        factoryBean.setJobDataMap(jobDataMap);

        try {
            factoryBean.afterPropertiesSet();
        } catch (ParseException e) {
            log.error(e.getMessage(), e);
        }
        return factoryBean.getObject();
    }
}

