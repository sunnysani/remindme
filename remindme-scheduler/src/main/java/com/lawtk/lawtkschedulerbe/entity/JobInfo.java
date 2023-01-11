package com.lawtk.lawtkschedulerbe.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lawtk.lawtkschedulerbe.util.AbstractEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Table;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table("job_info")
public class JobInfo extends AbstractEntity<JobInfo, Integer> {
    @Id
//    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;

    private String jobName;
    private String jobGroup;
    private String jobStatus;
    private String jobClass;
    private String cronExpression;
    private String description;
    private Boolean cronJob;
    private Integer scheduleId;
    private String baseJobName;

    private Integer scheduleTime;
    private String scheduleInterval;
    private String username;
}
