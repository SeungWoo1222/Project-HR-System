package com.woosan.hr_system.schedule.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class BusinessTrip {
    private int tripId;
    private int taskId;
    private String address;
    private String detailedAddress;
    private LocalDateTime createdDate;
    private String status;
    private String clientName;
    private String contactTel;
    private String contactEmail;
    private String note;
}
