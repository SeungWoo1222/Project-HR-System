package com.woosan.hr_system.report.dao;

import com.woosan.hr_system.report.model.Report;
import java.util.List;


public interface ReportDAO {
    List<Report> getAllReports(); // 모든 보고서 조회
    Report getReportById(int reportId); // 특정 보고서 조회
    void insertReport(Report report); // 보고서 작성
    void updateReport(Report report); // 보고서 수정
    void deleteReport(int reportId); // 보고서 삭제
}
