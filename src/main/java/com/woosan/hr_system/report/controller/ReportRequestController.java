package com.woosan.hr_system.report.controller;

import com.woosan.hr_system.report.model.Report;
import com.woosan.hr_system.report.model.ReportRequest;
import com.woosan.hr_system.report.service.ReportRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequestMapping("/reportRequest")
public class ReportRequestController {

    @Autowired
    private ReportRequestService reportRequestService;

    @PostMapping("/create") // 요청 생성
    public String createReportRequest(@RequestBody ReportRequest request) {
        reportRequestService.createReportRequest(request);
        return "Report request created successfully";
    }

    @GetMapping("/{id}") // 보고서 기반 작성 요청 조회
    public ReportRequest getReportRequestById(@PathVariable int id) {
        return reportRequestService.getReportRequestById(id);
    }

    @PutMapping("/update") // 작성 요청 수정
    public String updateReportRequest(@RequestBody ReportRequest request) {
        reportRequestService.updateReportRequest(request);
        return "Report request updated successfully";
    }

    @DeleteMapping("/{id}") // 특정 작성 요청 삭제
    public String deleteReportRequest(@PathVariable int id) {
        reportRequestService.deleteReportRequest(id);
        return "Report request deleted successfully";
    }

    @GetMapping("/employee/{employeeId}") // 사원 기반 보고서 조회
    public List<ReportRequest> getReportRequestsByEmployeeId(@PathVariable String employeeId) {
        return reportRequestService.getReportRequestsByEmployeeId(employeeId);
    }
}
