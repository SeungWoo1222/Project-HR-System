package com.woosan.hr_system.report.service;

import com.woosan.hr_system.auth.CustomUserDetails;
import com.woosan.hr_system.employee.model.Employee;
import com.woosan.hr_system.report.dao.RequestDAO;
import com.woosan.hr_system.report.model.Report;
import com.woosan.hr_system.report.model.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class RequestServiceImpl implements RequestService {

    @Autowired
    private RequestDAO requestDAO;

    @Override // 요청 생성
    public void createRequest(List<String> writerIds, LocalDate dueDate, String requestNote, String requesterId) {
        List<Request> requests = new ArrayList<>();
        LocalDateTime requestDate = LocalDateTime.now(); // 현재 기준 생성 시간 설정

        for (String writerId : writerIds) {
            Request request = new Request();
            request.setRequesterId(requesterId);
            request.setWriterId(writerId);
            request.setDueDate(dueDate);
            request.setRequestNote(requestNote);
            request.setRequestDate(requestDate);
            requests.add(request);
        }
        requestDAO.createRequest(requests);
    }

    @Override // 모든 요청 조회
    public List<Request> getAllRequests() {
        return requestDAO.getAllRequests();
    }

    @Override // 내가 작성한 요청 조회
    public List<Request> getMyRequests(String requesterId) {
        return requestDAO.getMyRequests(requesterId);
    }

    @Override // 특정 요청 조회
    public Request getRequestById(Long requestId) {
        return requestDAO.getRequestById(requestId);
    }


    @Override // 보고서 결재 처리
    public void updateApprovalStatus(Long reportId, String status, String rejectionReason) {
        // report 객체 설정
        Report report = new Report();
        report.setReportId(reportId);
        report.setStatus(status);
        report.setRejectReason(rejectionReason);

        requestDAO.updateApprovalStatus(report);
    }

    @Override // 요청 수정
    public void updateRequest(Long requestId, String writerId, String requestNote, LocalDate dueDate) {
        //request 객체 설정
        Request request = new Request();
        request.setRequestId(requestId);
        request.setWriterId(writerId);
        request.setRequestNote(requestNote);
        request.setDueDate(dueDate);

        LocalDateTime modifiedDate = LocalDateTime.now(); //현재 기준 수정 시간 설정
        request.setModifiedDate(modifiedDate);

        requestDAO.updateRequest(request);
    }

    @Override // 요청 삭제
    public void deleteRequest(Long requestId) {
        requestDAO.deleteRequest(requestId);
    }
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

