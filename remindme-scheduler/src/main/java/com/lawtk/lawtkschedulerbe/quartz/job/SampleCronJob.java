package com.lawtk.lawtkschedulerbe.quartz.job;

import com.lawtk.lawtkschedulerbe.entity.NotificationDto;
import com.lawtk.lawtkschedulerbe.util.AMQPUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.amqp.core.*;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.time.Instant;
import java.util.HashMap;

@Slf4j
@DisallowConcurrentExecution
public class SampleCronJob extends QuartzJobBean {

    private Queue queue;

    private void createQueue(String username) {
        this.queue = new Queue(username, false, false, true);
        var amqpAdmin = AMQPUtils.getAmqpAdmin();
        amqpAdmin.declareQueue(queue);
    }

    @SneakyThrows
    @Override
    protected void executeInternal(JobExecutionContext context) {
        var username = context.getTrigger().getJobDataMap().getString("username");
        var activityName = context.getTrigger().getJobDataMap().getString("activity_name");

        if (this.queue == null) {
            createQueue(username);
        }
        var rabbitTemplate = AMQPUtils.getRabbitTemplate();

        var notification = new NotificationDto(activityName);
        rabbitTemplate.convertAndSend(username, AMQPUtils.getObjectMapper().writeValueAsString(notification));

        log.info("Sending notification queue for " + activityName + " to " + username + " at " + Instant.now().toString());
    }
}
