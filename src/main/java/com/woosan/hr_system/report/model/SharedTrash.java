package com.woosan.hr_system.report.model;


import java.sql.Date;
import java.sql.Timestamp;

public class SharedTrash {

    private Long id;
    private String originalTable;
    private Long originalId;
    private Timestamp deletedDate;
    private Timestamp createdDate;
    private String deletedBy;
    private String content;
    private String approverWriterId;
    private String approverWriterName;
    private Integer fileId;
    private Date completionDueDate;
    private String title;
    private String status;
    private String rejectReason;
    private Long reportId;
    private String cloudServerFileUrl;
    private String originalFileName;
    private String uuidFileName;
    private Integer size;
    private Timestamp modifiedDate;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOriginalTable() {
        return originalTable;
    }

    public void setOriginalTable(String originalTable) {
        this.originalTable = originalTable;
    }

    public Long getOriginalId() {
        return originalId;
    }

    public void setOriginalId(Long originalId) {
        this.originalId = originalId;
    }

    public Timestamp getDeletedDate() {
        return deletedDate;
    }

    public void setDeletedDate(Timestamp deletedDate) {
        this.deletedDate = deletedDate;
    }

    public Timestamp getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Timestamp createdDate) {
        this.createdDate = createdDate;
    }

    public String getDeletedBy() {
        return deletedBy;
    }

    public void setDeletedBy(String deletedBy) {
        this.deletedBy = deletedBy;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getApproverWriterId() {
        return approverWriterId;
    }

    public void setApproverWriterId(String approverWriterId) {
        this.approverWriterId = approverWriterId;
    }

    public String getApproverWriterName() {
        return approverWriterName;
    }

    public void setApproverWriterName(String approverWriterName) {
        this.approverWriterName = approverWriterName;
    }

    public Integer getFileId() {
        return fileId;
    }

    public void setFileId(Integer fileId) {
        this.fileId = fileId;
    }

    public Date getCompletionDueDate() {
        return completionDueDate;
    }

    public void setCompletionDueDate(Date completionDueDate) {
        this.completionDueDate = completionDueDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRejectReason() {
        return rejectReason;
    }

    public void setRejectReason(String rejectReason) {
        this.rejectReason = rejectReason;
    }

    public Long getReportId() {
        return reportId;
    }

    public void setReportId(Long reportId) {
        this.reportId = reportId;
    }

    public String getCloudServerFileUrl() {
        return cloudServerFileUrl;
    }

    public void setCloudServerFileUrl(String cloudServerFileUrl) {
        this.cloudServerFileUrl = cloudServerFileUrl;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

    public String getUuidFileName() {
        return uuidFileName;
    }

    public void setUuidFileName(String uuidFileName) {
        this.uuidFileName = uuidFileName;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Timestamp getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Timestamp modifiedDate) {
        this.modifiedDate = modifiedDate;
    }
}

