package com.woosan.hr_system.report.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ReportFileService {
    void createReportFile(int reportId, int fileId);
    void deleteReportFile(int reportId);

    // reportId로 fileId를 가져옴
    List<Integer> getFileIdsByReportId(int reportId);

}
