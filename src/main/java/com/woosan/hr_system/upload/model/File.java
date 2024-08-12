package com.woosan.hr_system.upload.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class File {
    private int fileId;
    private String originalFileName;
    private String storedFileName;
    private long fileSize;
    private LocalDateTime uploadDate;
    private String uploadedBy;
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



