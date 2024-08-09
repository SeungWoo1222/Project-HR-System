package com.woosan.hr_system.employee.model;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class Resignation {
    private String employeeId;
    private String resignationReason;
    private String codeNumber;
    private String specificReason;
    private LocalDate resignationDate;
    private String resignationDocuments;
    private String processedBy;
    private LocalDateTime processedDate;
}
