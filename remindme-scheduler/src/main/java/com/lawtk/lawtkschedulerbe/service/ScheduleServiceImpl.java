package com.lawtk.lawtkschedulerbe.service;

import com.lawtk.lawtkschedulerbe.entity.JobInfo;
import com.lawtk.lawtkschedulerbe.entity.Schedule;
import com.lawtk.lawtkschedulerbe.repository.JobInfoRepository;
import com.lawtk.lawtkschedulerbe.repository.ScheduleRepository;
import com.lawtk.lawtkschedulerbe.util.EntityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
//@Transactional
public class ScheduleServiceImpl implements ScheduleService {
    @Autowired
    ScheduleRepository scheduleRepository;

    @Autowired
    JobInfoService jobInfoService;

    @Autowired
    JobInfoRepository jobInfoRepository;

    public Schedule addSchedule(Schedule schedule){
        JobInfo jobInfo = new JobInfo();
        scheduleRepository.save(EntityUtils.persistable(schedule, true));

        jobInfo.setId(schedule.getId());
        jobInfo.setJobName(schedule.getActivityName());
        jobInfo.setJobGroup("CronJob");
        jobInfo.setJobStatus("NORMAL");
        jobInfo.setJobClass("CronJob");
        jobInfo.setDescription("Job for activity: " + schedule.getActivityName());
        jobInfo.setCronJob(true);
        jobInfo.setScheduleId(schedule.getId());
        jobInfo.setBaseJobName(schedule.getActivityName());

        jobInfo.setScheduleTime(schedule.getScheduleTime());
        jobInfo.setScheduleInterval(schedule.getScheduleInterval());
        jobInfo.setUsername(schedule.getUsername());

        String cronExpression = createCronExpression(schedule.getScheduleTime(), schedule.getScheduleInterval());

        jobInfo.setCronExpression(cronExpression);

        jobInfoService.saveOrUpdate(jobInfo);

        return schedule;
    }

    public Schedule retrieveScheduleById(Integer id){
        return scheduleRepository.findById(id).get();
    }

    public List<Schedule> retrieveAllSchedule(String username){
        return scheduleRepository.findAllByUsername(username);
    }

    public Schedule updateSchedule(Schedule schedule){
        scheduleRepository.save(schedule);

        JobInfo jobInfo = jobInfoRepository.findById(schedule.getId()).get();

        //template cron, need affirmation//
//        String cronExpression = "0/10 * * * * ?";

        String cronExpression = createCronExpression(schedule.getScheduleTime(), schedule.getScheduleInterval());

        jobInfo.setCronExpression(cronExpression);
        jobInfo.setJobName(schedule.getActivityName());
        jobInfo.setCronJob(true);
        jobInfoService.updateScheduleJob(jobInfo);

        return schedule;
    }

    public void deleteSchedule(Schedule schedule){
        JobInfo jobInfo = jobInfoRepository.findById(schedule.getId()).get();
        jobInfoService.deleteJob(jobInfo);
        scheduleRepository.delete(schedule);
    }

    public String createCronExpression(Integer time, String interval){
        String cronExpression = "";
        Date date = new Date();
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        int day   = localDate.getDayOfMonth();
        int month = localDate.getMonthValue();


        switch (interval) {
            case "Minutes":
                if (time >= 0 && time<= 60){
                    cronExpression = String.format("0 */%d * ? * *", time);
                }else{
                    System.out.println("ERROR");
                }
                break;
            case "Hours":
                if (time >= 0 && time<= 24){
                    cronExpression = String.format("0 0 */%d ? * *", time);
                }else{
                    System.out.println("ERROR");
                }
                break;
            case "Days":
                if (time >= 1 && time<= 31){
                    cronExpression = String.format("0 0 0 %d/%d * ?",day, time);
                }else{
                    System.out.println("ERROR");
                }
                break;
            case "Weeks":
                if (time >= 1 && time<= 4){
                    cronExpression = String.format("0 0 0 %d/%d * ?",day, time*7);
                }else{
                    System.out.println("ERROR");
                }
                break;
            case "Months":
                if (time >= 1 && time<= 12){
                    cronExpression = String.format("0 0 0 %d %d/%d ?", day, month, time);
                }else{
                    System.out.println("ERROR");
                }
                break;
        }

        if (cronExpression.equals("")){
            cronExpression = "ERROR";
        }

        return cronExpression;
    }
}
