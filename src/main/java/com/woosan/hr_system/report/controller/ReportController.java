package com.woosan.hr_system.report.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.woosan.hr_system.auth.service.AuthService;
import com.woosan.hr_system.employee.dao.EmployeeDAO;
import com.woosan.hr_system.employee.model.Employee;
import com.woosan.hr_system.report.model.Report;
import com.woosan.hr_system.report.model.ReportStat;
import com.woosan.hr_system.report.model.Request;
import com.woosan.hr_system.report.service.ReportService;
import com.woosan.hr_system.report.service.RequestService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
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
    @Autowired
    private AuthService authService;

    @GetMapping("/main") // main 페이지 이동
    public String getMainPage(HttpSession session,
                              Model model) throws JsonProcessingException {

        // 로그인한 계정 기준 employee_id를 writerId(작성자)로 설정
        String employeeId = authService.getAuthenticatedUser().getUsername();

        // 내가 쓴 보고서 조회
        String reportStart = (String) session.getAttribute("staffReportStart");
        String reportEnd = (String) session.getAttribute("staffReportEnd");
        List<Report> reports = reportService.getRecentReports(reportStart, reportEnd, employeeId);
        model.addAttribute("reports", reports);

        // 내게 온 요청 조회
        String requestStart = (String) session.getAttribute("staffRequestStart");
        String requestEnd = (String) session.getAttribute("staffRequestEnd");
        List<Request> requests = requestService.getMyPendingRequests(requestStart, requestEnd, employeeId);
        model.addAttribute("requests", requests);

        // 보고서 통계
        String statisticStart = (String) session.getAttribute("staffStatisticStart");
        String statisticEnd = (String) session.getAttribute("staffStatisticEnd");

        List<String> employeeIds = Collections.singletonList(employeeId); // employeeId를 List<String>으로 변환 후 전달
        List<ReportStat> stats = reportService.getReportStats(statisticStart, statisticEnd, employeeIds);

        // 통계 View 관련 로직
        List<Object[]> statsArray = new ArrayList<>(); // JSON 변환
        statsArray.add(new Object[]{"월 별 보고서 통계", "총 보고서 수", "완료된 보고서 수", "미완료된 보고서 수"});
        for (ReportStat stat : stats) {
            statsArray.add(new Object[]{stat.getMonth(), stat.getTotal(), stat.getFinished(), stat.getUnfinished()});
        }
        String statsJson = objectMapper.writeValueAsString(statsArray);
        model.addAttribute("statsJson", statsJson);

        return "report/main"; // main.html 반환
    }

    @GetMapping("/list") // 보고서 리스트 페이지 이동
    public String showReportList(HttpSession session,
                                 Model model) {
        // 로그인한 계정 기준 employee_id를 writerId(작성자)로 설정
        String employeeId = authService.getAuthenticatedUser().getUsername();

        String reportStart = (String) session.getAttribute("staffReportStart");
        String reportEnd = (String) session.getAttribute("staffReportEnd");

        List<Report> reports = reportService.getAllReports(reportStart, reportEnd, employeeId);
        model.addAttribute("reports", reports);

        return "report/report-list";
    }

    @GetMapping("/write") // 보고서 생성 페이지 이동
    public String showCreatePage(Model model) {
        List<Employee> employees = employeeDAO.getAllEmployees();
        model.addAttribute("employees", employees); // employees 목록 추가
        model.addAttribute("report", new Report());
        return "report/write";
    }

    @PostMapping("/write") // 보고서 생성
    public String createReport(@ModelAttribute Report report){
//                               @RequestParam("file") MultipartFile file) {
        // 현재 로그인한 계정의 employeeId를 요청자(requesterId)로 설정
        String writerId = authService.getAuthenticatedUser().getUsername();
        report.setWriterId(writerId);
//        reportService.createReport(report, file);
        reportService.createReport(report);

        return "redirect:/report/main";
    }

    // 내가 작성한 보고서 날짜 설정 페이지 이동
    @GetMapping("/reportDate")
    public String showReportDatePage() {
        return "report/report-date";
    }

    // 내가 작성한 보고서 날짜 설정
    @PostMapping("/reportDate")
    public String setReportDateRange(@RequestParam(name = "reportStart") String reportStart,
                                          @RequestParam(name = "reportEnd") String reportEnd,
                                          HttpSession session) {

        session.setAttribute("staffReportStart", reportStart);
        session.setAttribute("staffReportEnd", reportEnd);
        return "redirect:/report/main";
    }

    // 나에게 온 요청 날짜 설정 페이지 이동
    @GetMapping("/requestDate")
    public String showRequestDatePage() {
        return "/report/request-date";
    }

    // 나에게 온 요청 날짜 설정
    @PostMapping("/requestDate")
    public String setRequestDateRange(@RequestParam(name = "requestStart") String requestStart,
                                           @RequestParam(name = "requestEnd") String requestEnd,
                                           HttpSession session) {

        session.setAttribute("staffRequestStart", requestStart);
        session.setAttribute("staffRequestEnd", requestEnd);
        return "redirect:/report/main";
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
    public String deleteReport(@RequestParam("reportId") Long reportId,
                               RedirectAttributes redirectAttributes) {
        reportService.deleteReport(reportId);
        return "redirect:/report/main";
    }

//=====================================================통계 메소드들======================================================

    @GetMapping("/statistic") // 통계 날짜설정 페이지 이동
    public String showStatisticPage() {
        return "report/statistic";
    }

    @GetMapping("/stats") // 통계 날짜 설정
    public String getReportStats(@RequestParam(name = "statisticStart") String statisticStart,
                                 @RequestParam(name = "statisticEnd") String statisticEnd,
                                 HttpSession session) {
        // 날짜 설정
        session.setAttribute("staffStatisticStart", statisticStart);
        session.setAttribute("staffStatisticEnd", statisticEnd);

        return "redirect:/report/main";
    }
//=====================================================통계 메소드들======================================================

}
