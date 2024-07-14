package com.woosan.hr_system.report.service;

import com.woosan.hr_system.report.model.ReportRequest;
import java.util.List;

public interface ReportRequestService {
    void createReportRequest(ReportRequest request);
    ReportRequest getReportRequestById(int requestId);
    void updateReportRequest(ReportRequest request);
    void deleteReportRequest(int requestId);
    List<ReportRequest> getReportRequestsByEmployeeId(String employeeId);
}
