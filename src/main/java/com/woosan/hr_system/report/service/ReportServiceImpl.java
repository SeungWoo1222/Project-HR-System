package com.woosan.hr_system.report.service;

import com.woosan.hr_system.report.dao.ReportDAO;
import com.woosan.hr_system.report.model.Report;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private ReportDAO reportDAO;

    @Override // 모든 보고서 조회
    public List<Report> getAllReports() {
        return reportDAO.getAllReports();
    }

    @Override // id를 이용한 특정 보고서 조회
    public Report getReportById(int reportId) {
        return reportDAO.getReportById(reportId);
    }

    @Override // 보고서 등록
    public void insertReport(Report report) {
        reportDAO.insertReport(report);
    }

    @Override // 보고서 수정
    public void updateReport(Report report) {
        reportDAO.updateReport(report);
    }

    @Override // 보고서 삭제
    public void deleteReport(int reportId) {
        reportDAO.deleteReport(reportId);
    }
}
