package com.woosan.hr_system.employee.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Termination {
    private String employeeId;
    private String terminationReason;
    private LocalDate terminationDate;
    private String terminationDocuments;
    private String processedBy;
    private LocalDateTime terminationProcessedDate;

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public LocalDate getTerminationDate() {
        return terminationDate;
    }

    public void setTerminationDate(LocalDate terminationDate) {
        this.terminationDate = terminationDate;
    }

    public String getTerminationReason() {
        return terminationReason;
    }

    public void setTerminationReason(String terminationReason) {
        this.terminationReason = terminationReason;
    }

    public String getTerminationDocuments() {
        return terminationDocuments;
    }

    public void setTerminationDocuments(String terminationDocuments) {
        this.terminationDocuments = terminationDocuments;
    }

    public String getProcessedBy() {
        return processedBy;
    }

    public void setProcessedBy(String processedBy) {
        this.processedBy = processedBy;
    }

    public LocalDateTime getTerminationProcessedDate() {
        return terminationProcessedDate;
    }

    public void setTerminationProcessedDate(LocalDateTime terminationProcessedDate) {
        this.terminationProcessedDate = terminationProcessedDate;
    }
}
