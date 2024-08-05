package com.woosan.hr_system.report.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class Request {
    private Long requestId;
    private Long reportId;
    private String requesterId;
    private String writerId;
    private String writerName;
    private LocalDateTime requestDate;
    private LocalDateTime modifiedDate;
    private LocalDate dueDate;
    private String requestNote;

    // 보고서작성, 요청 등 임원 선택에서 여러 임원을 받아오기 위한 변수
    private List<String> writerNameList;
    private List<String> writerIdList;






    // method
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

    public String getWriterId() {
        return writerId;
    }

    public void setWriterId(String writerId) {
        this.writerId = writerId;
    }

    public String getWriterName() {
        return writerName;
    }

    public void setWriterName(String writerName) {
        this.writerName = writerName;
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


    public List<String> getWriterIdList() {
        return writerIdList;
    }

    public void setWriterIdList(List<String> writerIdList) {
        this.writerIdList = writerIdList;
    }

    public List<String> getWriterNameList() {
        return writerNameList;
    }

    public void setWriterNameList(List<String> writerNameList) {
        this.writerNameList = writerNameList;
    }
}
