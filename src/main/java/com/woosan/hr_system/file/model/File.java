package com.woosan.hr_system.file.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class File {
    private int fileId;
    private final String originalFileName;
    private String storedFileName;
    private final long fileSize;
    private final LocalDateTime uploadDate;
    private final String uploadedBy;
    private String fileIdUsage;

    // 생성자
    public File(String originalFileName, String storedFileName, long fileSize, LocalDateTime uploadDate, String uploadedBy, String fileIdUsage) {
        this.originalFileName = originalFileName;
        this.storedFileName = storedFileName;
        this.fileSize = fileSize;
        this.uploadDate = uploadDate;
        this.uploadedBy = uploadedBy;
        this.fileIdUsage = fileIdUsage;
    }
}



