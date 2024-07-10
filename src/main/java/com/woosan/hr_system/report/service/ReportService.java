package com.woosan.hr_system.report.service;

import com.woosan.hr_system.report.model.Report;
import java.util.List;

public interface ReportService {
    List<Report> getAllReports();
    Report getReportById(int reportId);
    void insertReport(Report report);
    void updateReport(Report report);
    void deleteReport(int reportId);
}
