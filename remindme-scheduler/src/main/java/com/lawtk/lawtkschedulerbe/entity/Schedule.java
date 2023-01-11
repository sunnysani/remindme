package com.lawtk.lawtkschedulerbe.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
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
@Table("schedule")
public class Schedule extends AbstractEntity<Schedule, Integer> {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;

    @JsonProperty(value = "activity_name", required = true)
    private String activityName;
    @JsonProperty(value = "schedule_time", required = true)
    private Integer scheduleTime;
    @JsonProperty(value = "schedule_interval", required = true)
    private String scheduleInterval;
    @JsonProperty("username")
    private String username;

}
