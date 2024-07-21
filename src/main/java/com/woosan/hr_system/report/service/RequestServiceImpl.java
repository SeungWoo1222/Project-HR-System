package com.woosan.hr_system.report.service;

import com.woosan.hr_system.report.dao.RequestDAO;
import com.woosan.hr_system.report.model.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class RequestServiceImpl implements RequestService {

    @Autowired
    private RequestDAO requestDAO;

    @Override // 요청 생성
    public void createRequest(Request request) {
        requestDAO.createRequest(request);
    }

    @Override // 모든 요청 조회
    public List<Request> getAllRequests() {
        return requestDAO.getAllRequests();
    }

    @Override // 특정 요청 조회
    public Request getRequestById(Long requestId) {
        return requestDAO.getRequestById(requestId);
    }

    @Override // 요청 수정
    public void updateRequest(Request request) {
        requestDAO.updateRequest(request);
    }

    @Override // 요청 삭제
    public void deleteRequest(Long requestId) {
        requestDAO.deleteRequest(requestId);
    }
















//    @Override // 작성 요청 생성
//    public void createReportRequest(ReportRequest request) {
//        reportRequestDAO.createReportRequest(request);
//    }
//
//    @Override // 보고서 기반 작성 요청 조회
//    public ReportRequest getReportRequestById(int requestId) {
//        return reportRequestDAO.getReportRequestById(requestId);
//    }
//
//    @Override // 작성 요청 수정
//    public void updateReportRequest(ReportRequest request) {
//        reportRequestDAO.updateReportRequest(request);
//    }
//
//    @Override // 특정 작성 요청 삭제
//    public void deleteReportRequest(int requestId) {
//        reportRequestDAO.deleteReportRequest(requestId);
//    }
//
//    @Override // 사원 기반 보고서 조회
//    public List<ReportRequest> getReportRequestsByEmployeeId(String employeeId) {
//        return reportRequestDAO.getReportRequestsByEmployeeId(employeeId);
//    }
}
