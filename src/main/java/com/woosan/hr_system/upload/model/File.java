package com.woosan.hr_system.upload.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class File {
    private int fileId;
    private final String originalFileName;
    private String storedFileName;
    private final long fileSize;
    private final LocalDateTime uploadDate;
    private final String uploadedBy;
    private String usage;

    // 생성자
    public File(String originalFileName, String storedFileName, long fileSize, LocalDateTime uploadDate, String uploadedBy, String usage) {
        this.originalFileName = originalFileName;
        this.storedFileName = storedFileName;
        this.fileSize = fileSize;
        this.uploadDate = uploadDate;
        this.uploadedBy = uploadedBy;
        this.usage = usage;
    }
}



