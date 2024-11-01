package com.woosan.hr_system.report.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileArchive {
    int fileId;
    int reportId;
    LocalDateTime deletedDate;
    String originalFileName;
    String storedFileName;
    Long fileSize;
    LocalDateTime uploadedAt;
    String uploadedBy;
    String fileIdUsage;
}
