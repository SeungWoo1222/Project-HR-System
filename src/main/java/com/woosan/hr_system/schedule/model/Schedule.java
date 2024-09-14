package com.woosan.hr_system.schedule.model;

import ch.qos.logback.core.status.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Schedule {
    private int taskId;
    private String memberId;
    private String taskName;
    private String content;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
    private LocalDateTime createdDate;
    private Integer projectId;
    private Integer mapId;
}
