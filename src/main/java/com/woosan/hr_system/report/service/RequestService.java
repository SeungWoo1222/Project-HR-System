package com.woosan.hr_system.report.service;

import com.woosan.hr_system.employee.model.Employee;
import com.woosan.hr_system.report.model.Report;
import com.woosan.hr_system.report.model.Request;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface RequestService {
    void createRequest(List<String> writerIds, LocalDate dueDate, String requestNote, String requesterId);

    List<Request> getAllRequests();
    Request getRequestById(Long requestId);
    List<Request> getMyRequests(String requesterId);

    // 보고서 결재 처리
    void updateApprovalStatus(Long reportId, String status, String rejectionReasont);
    void updateRequest(Long requestId, List<String> writerIds, String requestNote, LocalDate dueDate);

    void deleteRequest(Long requestId);

}
