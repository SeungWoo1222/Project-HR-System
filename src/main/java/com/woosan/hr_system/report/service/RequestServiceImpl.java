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
    public void createRequest(String employeeId, LocalDate dueDateSql, String requestNote, LocalDateTime requestDate) {
        Request request = new Request();
        LocalDateTime now = LocalDateTime.now();

        request.setRequestDate(now);
        request.setEmployeeId(employeeId);
        request.setDueDate(dueDateSql);
        request.setRequestNote(requestNote);
        requestDAO.insertRequest(request);
    }

    @Override // 모든 요청 조회
    public List<Request> getAllReportRequests() {
        return requestDAO.getAllReportRequests();
    }

    @Override // 특정 요청 조회
    public Request getRequestById(Long requestId) {
        return requestDAO.getRequestById(requestId);
    }

    @Override
    public void updateRequest(Long requestId, String employeeId,String requestNote,LocalDate dueDate) {
        Request request = requestDAO.getRequestById(requestId);
        request.setEmployeeId(employeeId);
        request.setRequestNote(requestNote);
        request.setDueDate(dueDate);
        requestDAO.updateRequest(request);
    }

    @Override
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
