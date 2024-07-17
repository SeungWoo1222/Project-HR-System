package com.woosan.hr_system.employee.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Resignation {
    private String employeeId;
    private String resignationReason;
    private LocalDate resignationDate;
    private String resignationDocuments;
    private String processedBy;
    private LocalDateTime processedDate;

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public LocalDate getResignationDate() {
        return resignationDate;
    }

    public void setResignationDate(LocalDate resignationDate) {
        this.resignationDate = resignationDate;
    }

    public String getResignationReason() {
        return resignationReason;
    }

    public void setResignationReason(String resignationReason) {
        this.resignationReason = resignationReason;
    }

    public String getResignationDocuments() {
        return resignationDocuments;
    }

    public void setResignationDocuments(String resignationDocuments) {
        this.resignationDocuments = resignationDocuments;
    }

    public String getProcessedBy() {
        return processedBy;
    }

    public void setProcessedBy(String processedBy) {
        this.processedBy = processedBy;
    }

    public LocalDateTime getProcessedDate() {
        return processedDate;
    }

    public void setProcessedDate(LocalDateTime processedDate) {
        this.processedDate = processedDate;
    }
}
