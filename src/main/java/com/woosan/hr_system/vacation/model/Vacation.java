package com.woosan.hr_system.vacation.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Vacation {
    private int vacationId;
    private String employeeId;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private String vacationType;
    private String reason;
    private float usedDays;
    private String approvalStatus;
    private String processingBy;
    private LocalDateTime processingAt;
}
