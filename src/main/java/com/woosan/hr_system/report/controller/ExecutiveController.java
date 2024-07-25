package com.woosan.hr_system.report.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.woosan.hr_system.employee.dao.EmployeeDAO;
import com.woosan.hr_system.employee.model.Employee;
import com.woosan.hr_system.report.model.FileMetadata;
import com.woosan.hr_system.report.model.Report;
import com.woosan.hr_system.report.model.ReportStat;
import com.woosan.hr_system.report.model.Request;
import com.woosan.hr_system.report.service.ReportService;
import com.woosan.hr_system.report.service.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin/request")
public class ExecutiveController {
    @Autowired
    private ReportService reportService;
    @Autowired
    private RequestService requestService;
    @Autowired
    private EmployeeDAO employeeDAO;
    @Autowired
    private ObjectMapper objectMapper; // 통계 모델 반환 후 JSON 변환용

    @GetMapping("/main") // main 페이지 이동
    public String getMainPage(@RequestParam(name = "startDate", required = false) String startDate,
                              @RequestParam(name = "endDate", required = false) String endDate,
                              Model model) throws JsonProcessingException {

        List<Report> reports = reportService.getPendingApprovalReports();
        List<Request> requests = requestService.getMyRequests();

        model.addAttribute("reports", reports);
        model.addAttribute("requests", requests);

        // 보고서 통계
        List<ReportStat> stats = reportService.getReportStats(startDate, endDate);
        List<Object[]> statsArray = new ArrayList<>(); // JSON 변환
        statsArray.add(new Object[]{"월 별 보고서 통계", "총 보고서 수", "완료된 보고서 수", "미완료된 보고서 수"});
        for (ReportStat stat : stats) {
            statsArray.add(new Object[]{stat.getMonth(), stat.getTotal(), stat.getFinished(), stat.getUnfinished()});
        }
        String statsJson = objectMapper.writeValueAsString(statsArray);
        model.addAttribute("statsJson", statsJson);

        return "admin/report/main"; // main.html 반환
    }
    @GetMapping("/statistic") // 통계 날짜, 임원 설정 페이지 이동
    public String showStatisticPage(Model model) {
        List<Employee> employees = employeeDAO.getAllEmployees();
        model.addAttribute("ReportStat", new Report());
        model.addAttribute("employees", employees); // employees 목록 추가
        return "/admin/report/statistic";
    }

    @GetMapping("/stats") // 통계 날짜, 임원 설정
    public String getReportStats(@RequestParam(name = "startDate") String startDate,
                                 @RequestParam(name = "endDate") String endDate,
                                 Model model) {
        // 날짜 설정
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // startDate와 endDate를 "yyyy-MM-dd" 형식으로 변환
        // 그 후 마지막날에 1달을 더하고 1일을 다시 뺌 (달마다 마지막 일수가 다름)
        // (ex endDate = 2024-08-01, +1Month -> 2024-09-01, -1minusDays -> 2024-08-30
        LocalDate start = LocalDate.parse(startDate + "-01");
        LocalDate end = LocalDate.parse(endDate + "-01").plusMonths(1).minusDays(1);

        String formattedStartDate = start.format(formatter);
        String formattedEndDate = end.format(formatter);

        model.addAttribute("startDate", formattedStartDate);
        model.addAttribute("endDate", formattedEndDate);

        // 임원 설정


        return "redirect:/admin/request/main?startDate=" + formattedStartDate + "&endDate=" + formattedEndDate;
    }

    @GetMapping("/{requestId}") // 특정 요청 조회
    public String viewRequest(@PathVariable("requestId") Long requestId, Model model) {
        Request request = requestService.getRequestById(requestId);
        model.addAttribute("request", request);
        return "admin/report/request_view";
    }


    @GetMapping("/edit") // 요청 수정 페이지 이동
    public String editRequest(@RequestParam(name = "requestId") Long requestId, Model model) {
        Request request = requestService.getRequestById(requestId);
        List<Employee> employees = employeeDAO.getAllEmployees();
        model.addAttribute("employees", employees); // employees 목록 추가
        model.addAttribute("request", request);
        return "/admin/report/request_edit";
    }

    @PostMapping("/edit") // 요청 수정
    public String updateRequest(@RequestParam("requestId") Long requestId,
                                @RequestParam("employeeId") String employeeId,
                                @RequestParam("requestNote") String requestNote,
                                @RequestParam("dueDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dueDate) {
        //request 객체 설정
        Request request = new Request();
        request.setRequestId(requestId);
        request.setEmployeeId(employeeId);
        request.setRequestNote(requestNote);
        request.setDueDate(dueDate);

        requestService.updateRequest(request);
        return "redirect:/admin/request/main";
    }

    @GetMapping("/write") // 요청 생성 페이지 이동
    public String showWritePage(Model model) {
        List<Employee> employees = employeeDAO.getAllEmployees();
        model.addAttribute("employees", employees); // employees 목록 추가
        return "admin/report/request_write";
    }

    @PostMapping("/write") // 요청 생성
    public String createRequest(@RequestParam("employeeName") String employeeName,
                                @RequestParam("dueDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dueDate,
                                @RequestParam("requestNote") String requestNote,
                                Model model) {
        try {
            // request 객체 설정
            Request request = new Request();

            request.setEmployeeName(employeeName);
            request.setDueDate(dueDate);
            request.setRequestNote(requestNote);

            requestService.createRequest(request);

            model.addAttribute("message", "보고서 작성 완료");
            return "redirect:/admin/request/main";
        } catch (DateTimeParseException e) {
            model.addAttribute("message", "날짜 형식이 잘못되었습니다.");
            return "/admin/report/request_write";
        }
    }

    @DeleteMapping("/delete/{requestId}") // 요청 삭제
    public String deleteRequest(@PathVariable("requestId") Long id, RedirectAttributes redirectAttributes) {
        requestService.deleteRequest(id);
        return "redirect:/admin/request/main";
    }


    // 보고서 맵핑

    @GetMapping("/report/{reportId}") // 특정 보고서 조회
    public String viewReport(@PathVariable("reportId") Long reportId, Model model) {
        Report report = reportService.getReportById(reportId);
        model.addAttribute("report", report);

        if (report.getFileId() != null) {
            FileMetadata reportFile = reportService.getReportFileById(report.getFileId());
            model.addAttribute("reportFile", reportFile);
        }
        return "/admin/report/report_view";
    }

    @PostMapping("/approve") // 결재 처리
    public String approveReport(@RequestParam("reportId") Long reportId,
                                @RequestParam("status") String status,
                                @RequestParam(name = "rejectionReason", required = false) String rejectionReason) {
        try {
            // report 객체 설정
            Report report = new Report();
            report.setReportId(reportId);
            report.setStatus(status);
            report.setRejectReason(rejectionReason);

            reportService.updateApprovalStatus(report);
            return "redirect:/admin/request/main";
        } catch (Exception e) {
            return "error"; // 에러 메시지 표시
        }
    }

}
