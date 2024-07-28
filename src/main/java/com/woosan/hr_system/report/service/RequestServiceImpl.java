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
    public void createRequest(List<String> writerIds, List<String> writerNames, LocalDate dueDate, String requestNote, String requesterId) {

        LocalDateTime requestDate = LocalDateTime.now(); // 현재 기준 생성 시간 설정
        List<Request> requests = new ArrayList<>();

        // requests 객체 설정
        for (int i = 0; i < writerIds.size(); i++) {
            Request request = new Request();
            request.setRequesterId(requesterId);
            request.setWriterId(writerIds.get(i));
            request.setWriterName(writerNames.get(i));
            request.setDueDate(dueDate);
            request.setRequestNote(requestNote);
            request.setRequestDate(requestDate);
            requests.add(request);
            System.out.println("name : " + writerNames.get(i));
            System.out.println("id : " + writerIds.get(i));
        }

        requestDAO.createRequest(requests);
    }

    @Override // 모든 요청 조회
    public List<Request> getAllRequests() {
        return requestDAO.getAllRequests();
    }

    @Override  // 로그인한 계정 기준 요청 리스트 조회(내가 쓴 요청 리스트 조회)
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
    public void updateRequest(Long requestId, List<String> writerIds, List<String> writerNames, String requestNote, LocalDate dueDate) {

        //request 객체 설정
        LocalDateTime modifiedDate = LocalDateTime.now(); //현재 기준 수정 시간 설정
        List<Request> requests = new ArrayList<>();

        // 작성자가 한명인 경우 => 요청을 수정
        if (writerIds.size() == 1) {
            Request request = new Request();
            request.setRequestId(requestId);
            request.setWriterId(writerIds.get(0));
            request.setRequestNote(requestNote);
            request.setDueDate(dueDate);
            request.setModifiedDate(modifiedDate);
            requestDAO.updateRequest(request);
        }
        // 작성자가 여러명인 경우 => 요청 삭제 후 새로운 요청 생성
        else if (writerIds.size() > 1) {
            requestDAO.deleteRequest(requestId);
            String requesterId = null;
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
                CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
                requesterId = userDetails.getUsername();
            }

            for (int i = 0; i < writerIds.size(); i++) {
                Request request = new Request();
                request.setRequesterId(requesterId);
                request.setWriterId(writerIds.get(i));
                request.setWriterName(writerNames.get(i));
                request.setRequestNote(requestNote);
                request.setDueDate(dueDate);
                request.setRequestDate(modifiedDate);
                requests.add(request);
            }
            requestDAO.createRequest(requests);
        }
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

