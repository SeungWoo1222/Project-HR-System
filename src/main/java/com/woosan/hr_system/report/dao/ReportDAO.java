package com.woosan.hr_system.report.dao;

import com.woosan.hr_system.report.model.Report;
import com.woosan.hr_system.report.model.ReportStat;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface ReportDAO {
    int createReport(Map<String, Object> params);
    List<Report> getAllReports(String employeeId);
    Report getReportById(int reportId);
    List<ReportStat> getReportStats(LocalDate statisticStart, LocalDate statisticEnd, List<String> writerIdList);
    List<ReportStat> getReportStats(LocalDate statisticStart, LocalDate statisticEnd, String writerId);
    List<Report> getRecentReports(String writerId);
    List<Report> getUnprocessedReports(String approverId);
    List<Report> search(String keyword, int pageSize, int offset, String writerId, Integer searchType, String approvalStatus, String startDate, String endDate);
    int count(String keyword, String writerId, Integer searchType, String approvalStatus, String startDate, String endDate);
    List<Report> toApproveSearch(String keyword, int pageSize, int offset, String approverId, Integer searchType, String approvalStatus, String startDate, String endDate);
    int toApproveCount(String keyword, String approverId, Integer searchType, String approvalStatus, String startDate, String endDate);
    void updateReport(Report report);
    void updateApprovalStatus(Report report);
    void deleteReport(int reportId);
    void insertReportIntoSharedTrash(int reportId);
}
