package com.woosan.hr_system.report.service;

import com.woosan.hr_system.report.model.ReportStat;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import com.woosan.hr_system.report.model.Report;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import com.woosan.hr_system.report.model.FileMetadata;

public interface ReportService {
    // 조회 관련 메소드
    List<Report> getAllReports();
    Report getReportById(Long reportId);
    FileMetadata getReportFileById(Long fileId);

    // 생성 관련 메소드
    void createReport(Report report, MultipartFile file) throws IOException;
    List<FileMetadata> uploadFiles(Long reportId, MultipartFile[] files) throws IOException;

    // 보고서 수정 관련 메소드
    void updateReport(Report report);
    void updateApprovalStatus(Report report);

    // 보고서 삭제
    void deleteReport(Long id);

    List<ReportStat> getReportStats(String startDate, String endDate);

}
