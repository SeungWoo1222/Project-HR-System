package com.woosan.hr_system.report.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.woosan.hr_system.auth.CustomUserDetails;
import com.woosan.hr_system.employee.dao.EmployeeDAO;
import com.woosan.hr_system.employee.model.Employee;
import com.woosan.hr_system.employee.service.EmployeeService;
import com.woosan.hr_system.report.model.Report;
import com.woosan.hr_system.report.model.ReportStat;
import com.woosan.hr_system.report.model.Request;
import com.woosan.hr_system.report.service.RequestService;
import com.woosan.hr_system.report.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.woosan.hr_system.report.model.FileMetadata;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/report")
public class ReportController {
    @Autowired
    private ReportService reportService;
    @Autowired
    private RequestService requestService;
    @Autowired
    private EmployeeDAO employeeDAO;
    @Autowired
    private ObjectMapper objectMapper; // 통계 모델 반환 후 JSON 반환용


    @GetMapping("/write") // 보고서 생성 페이지 이동
    public String showCreatePage(Model model) {
        List<Employee> employees = employeeDAO.getAllEmployees();
        model.addAttribute("employees", employees); // employees 목록 추가
        return "report/write";
    }

    @PostMapping("/write") // 보고서 생성
    public String createReport(@RequestParam("title") String title,
                               @RequestParam("content") String content,
                               @RequestParam("writerId") List<String> approverIds,
                               @RequestParam("writerName") List<String> approverNames,
                               @RequestParam("completeDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate completeDate,
                               @RequestParam("file") MultipartFile file,
                               Model model) {
        // 현재 로그인한 계정의 employeeId를 요청자(requesterId)로 설정
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            String employeeId = userDetails.getUsername();
            reportService.createReport(title, content, approverIds, approverNames, completeDate, file, employeeId);
        }

        model.addAttribute("message", "보고서 작성 완료");
        return "redirect:/report/main";
    }

    @GetMapping("/main") // main 페이지 이동
    public String getMainPage(@RequestParam(name = "startDate", required = false) String startDate,
                              @RequestParam(name = "endDate", required = false) String endDate,
                              Model model) throws JsonProcessingException {
        // 보고서, 요청 list 생성
        // yy-mm-dd 반환 받을 객체 설정
        Report report = new Report();
        report.setFormattedCreatedDate(null);

        Request request = new Request();
        request.setFormattedRequestDate(null);
        request.setFormattedDueDate(null);

        List<Report> reports = reportService.getAllReports();
        List<Request> requests = requestService.getAllRequests();

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

        return "report/main"; // main.html 반환
    }

    @GetMapping("/statistic") // 통계 날짜설정 페이지 이동
    public String showStatisticPage(Model model) {
        model.addAttribute("ReportStat", new Report());
        return "report/statistic";
    }

    @GetMapping("/stats") // 통계 날짜 설정
    public String getReportStats(@RequestParam(name = "startDate") String startDate,
                                 @RequestParam(name = "endDate") String endDate,
                                 Model model) {
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
        return "redirect:/report/main?startDate=" + formattedStartDate + "&endDate=" + formattedEndDate;
    }


    @GetMapping("/edit") // 수정 페이지 이동
    public String updateReport(@RequestParam("reportId") Long reportId, Model model) {
        List<Employee> employees = employeeDAO.getAllEmployees();
        Report report = reportService.getReportById(reportId);
        model.addAttribute("employees", employees); // employees 목록 추가
        model.addAttribute("report", report);
        return "report/edit";
    }

    @PostMapping("/edit") // 보고서 수정
    public String updateReport(@RequestParam("reportId") Long reportId,
                               @RequestParam("title") String title,
                               @RequestParam("content") String content,
                               @RequestParam("approverId") String approverId,
                               @RequestParam("completeDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate completeDate) {

        // report 객체 설정
        Report report = new Report();
        report.setReportId(reportId);
        report.setTitle(title);
        report.setContent(content);
        report.setApproverId(approverId);
        report.setCompleteDate(completeDate);

        reportService.updateReport(report);
        return "redirect:/report/" + reportId;
    }

    @DeleteMapping("/delete/{reportId}") // 보고서 삭제
    public String deleteReport(@PathVariable("reportId") Long id,
                               RedirectAttributes redirectAttributes) {
        reportService.deleteReport(id);
        return "redirect:/report/main";
    }
}
