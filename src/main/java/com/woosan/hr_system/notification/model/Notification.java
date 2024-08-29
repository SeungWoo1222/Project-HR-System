package com.woosan.hr_system.notification.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Notification {
    private int notificationId;
    private String employeeId;
    private String message;
    private String url;
    private LocalDateTime createdAt;
    private Boolean readStatus;
    private LocalDateTime readAt;
}
