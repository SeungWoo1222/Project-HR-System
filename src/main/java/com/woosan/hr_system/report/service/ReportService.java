package com.woosan.hr_system.report.service;

import com.woosan.hr_system.report.model.Report;
import java.util.List;
import java.util.Map;

public interface ReportService {
    List<Report> getAllReports();
    Report getReportById(int reportId);
    void insertReport(Report report);
    void updateReport(Report report);
    void updateReportPartial(int reportId, Map<String, Object> updates);
    void deleteReport(int reportId);
}
