package com.woosan.hr_system.report.service;

import com.woosan.hr_system.report.model.Report;
import com.woosan.hr_system.report.model.ReportRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ReportRequestService {
    List<ReportRequest> getAllReportRequests();
    ReportRequest getRequestById(Long requestId);
    void updateRequest(Long requestId,String employeeId,String requestNote,LocalDate dueDate);
    void deleteRequest(Long requestId);
    void createRequest(String employeeId, LocalDate dueDateSql, String requestNote, LocalDateTime requestDate);


//    void insertRequest(ReportRequest request);
//    void createReportRequest(ReportRequest request);
//    ReportRequest getReportRequestById(int requestId);
//    void updateReportRequest(ReportRequest request);
//    void deleteReportRequest(int requestId);
//    List<ReportRequest> getReportRequestsByEmployeeId(String employeeId);
}
