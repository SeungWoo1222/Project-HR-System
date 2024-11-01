package com.woosan.hr_system.file.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class File {
    private int fileId;
    private String originalFileName;
    private String storedFileName;
    private long fileSize;
    private LocalDateTime uploadedAt;
    private String uploadedBy;
    private String fileIdUsage;

    // 생성자
    public File(String originalFileName, String storedFileName, long fileSize, LocalDateTime uploadedAt, String uploadedBy, String fileIdUsage) {
        this.originalFileName = originalFileName;
        this.storedFileName = storedFileName;
        this.fileSize = fileSize;
        this.uploadedAt = uploadedAt;
        this.uploadedBy = uploadedBy;
        this.fileIdUsage = fileIdUsage;
    }
}



