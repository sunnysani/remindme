package com.lawtk.lawtkschedulerbe.entity;


import com.lawtk.lawtkschedulerbe.util.AbstractEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table("notification")
public class Notification extends AbstractEntity<Notification, Integer> {
    @Id
    private Integer id;

    private String type;
    private LocalDateTime notificationTime;
    private Boolean isRead;
}
