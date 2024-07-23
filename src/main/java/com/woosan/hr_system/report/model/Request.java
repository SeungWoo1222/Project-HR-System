package com.woosan.hr_system.report.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Request {
    private Long requestId;
    private Long reportId;
    private String requesterId;
    private String employeeId;
    private LocalDateTime requestDate;
    private LocalDateTime modifiedDate;
    private LocalDate dueDate;
    private String requestNote;

    private String employeeName; // emplyees 테이블 name컬럼

    // main.html에 yy-mm-dd로 반환하는 변수
    private String formattedDueDate;
    private String formattedRequestDate;






    // Getters and Setters
    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public String getRequesterId() {
        return requesterId;
    }

    public void setRequesterId(String requesterId) {
        this.requesterId = requesterId;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public LocalDateTime getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(LocalDateTime requestDate) {
        this.requestDate = requestDate;
    }

    public LocalDateTime getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(LocalDateTime modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public String getRequestNote() {
        return requestNote;
    }

    public void setRequestNote(String requestNote) {
        this.requestNote = requestNote;
    }

    public Long getReportId() {
        return reportId;
    }

    public void setReportId(Long reportId) {
        this.reportId = reportId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getFormattedRequestDate() {
        return formattedRequestDate;
    }

    public void setFormattedRequestDate(String formattedrequestDate) {
        this.formattedRequestDate = formattedrequestDate;
    }

    public String getFormattedDueDate() {
        return formattedDueDate;
    }

    public void setFormattedDueDate(String formattedDueDate) {
        this.formattedDueDate = formattedDueDate;
    }




}
