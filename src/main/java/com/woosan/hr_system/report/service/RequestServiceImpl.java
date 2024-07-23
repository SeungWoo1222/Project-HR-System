package com.woosan.hr_system.report.service;

import com.woosan.hr_system.auth.CustomUserDetails;
import com.woosan.hr_system.employee.model.Employee;
import com.woosan.hr_system.report.dao.RequestDAO;
import com.woosan.hr_system.report.model.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
        // 현재 로그인 한 사용자 employeeId를 요청자(requester_id)로 설정
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            request.setRequesterId(userDetails.getUsername());
        }

        LocalDateTime requestDate = LocalDateTime.now(); //현재 기준 생성 시간 설정
        request.setRequestDate(requestDate);

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

    @Override // 모든 임원 조회
    public List<Employee> getEmployees() {
        return requestDAO.getAllEmployees();
    }

    @Override // 요청 수정
    public void updateRequest(Request request) {
        LocalDateTime modifiedDate = LocalDateTime.now(); //현재 기준 수정 시간 설정
        request.setModifiedDate(modifiedDate);

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
