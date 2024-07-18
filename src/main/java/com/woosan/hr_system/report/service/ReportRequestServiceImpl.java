package com.woosan.hr_system.report.service;

import com.woosan.hr_system.report.dao.ReportRequestDAO;
import com.woosan.hr_system.report.model.Report;
import com.woosan.hr_system.report.model.ReportRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ReportRequestServiceImpl implements ReportRequestService {

    @Autowired
    private ReportRequestDAO reportRequestDAO;

    @Override // 모든 요청 조회
    public List<ReportRequest> getAllReportRequests() {
        return reportRequestDAO.getAllReportRequests();
    }

    @Override // 특정 요청 조회
    public ReportRequest getRequestById(Long requestId) {
        return reportRequestDAO.getRequestById(requestId);
    }


    @Override // 작성 요청 생성
    public void createReportRequest(ReportRequest request) {
        reportRequestDAO.createReportRequest(request);
    }

    @Override // 보고서 기반 작성 요청 조회
    public ReportRequest getReportRequestById(int requestId) {
        return reportRequestDAO.getReportRequestById(requestId);
    }

    @Override // 작성 요청 수정
    public void updateReportRequest(ReportRequest request) {
        reportRequestDAO.updateReportRequest(request);
    }

    @Override // 특정 작성 요청 삭제
    public void deleteReportRequest(int requestId) {
        reportRequestDAO.deleteReportRequest(requestId);
    }

    @Override // 사원 기반 보고서 조회
    public List<ReportRequest> getReportRequestsByEmployeeId(String employeeId) {
        return reportRequestDAO.getReportRequestsByEmployeeId(employeeId);
    }
}
