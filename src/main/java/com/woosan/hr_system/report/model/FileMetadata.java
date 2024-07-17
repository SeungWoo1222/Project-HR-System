package com.woosan.hr_system.report.model;

import java.util.Date;

public class FileMetadata {
    private Long fileId;
    private String cloudServerFileUrl;
    private String originalFilename;
    private String uuidFilename;
    private long size;
    private Date uploadDate;

    public Long getId() {
        return fileId;
    }

    public void setId(Long fileId) {
        this.fileId = fileId;
    }

    public String getCloudServerFileUrl() {
        return cloudServerFileUrl;
    }

    public void setCloudServerFileUrl(String cloudServerFileUrl) {
        this.cloudServerFileUrl = cloudServerFileUrl;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
    }

    public String getUuidFilename() {
        return uuidFilename;
    }

    public void setUuidFilename(String uuidFilename) {
        this.uuidFilename = uuidFilename;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public Date getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(Date uploadDate) {
        this.uploadDate = uploadDate;
    }
}
