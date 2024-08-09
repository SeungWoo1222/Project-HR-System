package com.woosan.hr_system.report.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.woosan.hr_system.auth.service.AuthService;
import com.woosan.hr_system.employee.dao.EmployeeDAO;
import com.woosan.hr_system.employee.model.Employee;
import com.woosan.hr_system.report.model.FileMetadata;
import com.woosan.hr_system.report.model.Report;
import com.woosan.hr_system.report.model.ReportStat;
import com.woosan.hr_system.report.model.Request;
import com.woosan.hr_system.report.service.ReportService;
import com.woosan.hr_system.report.service.RequestService;
import com.woosan.hr_system.search.PageRequest;
import com.woosan.hr_system.search.PageResult;
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
    private ObjectMapper objectMapper; // 통계 반환 후 view에 보내면서 JSON로 반환함
    @Autowired
    private AuthService authService;
//=====================================================CRUD 메소드======================================================
    @GetMapping("/main") // main 페이지 이동
    public String getMainPage(HttpSession session,
                              Model model) throws JsonProcessingException {

        // 로그인한 계정 기준 employee_id를 writerId(작성자)로 설정
        String writerId = authService.getAuthenticatedUser().getUsername();

        // 내가 쓴 보고서 조회(최근 5개)
        List<Report> reports = reportService.getRecentReports(writerId);
        model.addAttribute("reports", reports);

        // 내게 온 요청 조회(최근 5개 + 보고서가 안쓰인 요청)
        List<Request> requests = requestService.getMyPendingRequests(writerId);
        model.addAttribute("requests", requests);

        // 보고서 통계
        String statisticStart = (String) session.getAttribute("staffStatisticStart");
        String statisticEnd = (String) session.getAttribute("staffStatisticEnd");

        List<String> employeeIds = Collections.singletonList(writerId); // employeeId를 List<String>으로 변환 후 전달
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

        return "redirect:/report/list";
    }

    @GetMapping("/writeFromRequest") // 요청 들어온 보고서 생성 페이지 이동
    public String showCreateFromRequestPage(@RequestParam("requestId") Long requestId,
                                            Model model) {
        Request request = requestService.getRequestById(requestId);
        model.addAttribute("request", request);
        model.addAttribute("report", new Report());
        return "report/writeFromRequest";
    }

    @PostMapping("/writeFromRequest") // 요청 들어온 보고서 생성
    public String CreateReportFromRequest(@ModelAttribute Report report,
                                        @RequestParam("approverId") String approverId){
//                               @RequestParam("file") MultipartFile file) {
        // 현재 로그인한 계정의 employeeId를 요청자(requesterId)로 설정
        String writerId = authService.getAuthenticatedUser().getUsername();
        report.setWriterId(writerId);
//        reportService.createReport(report, file);

        reportService.createReportFromRequest(report, approverId);

        return "redirect:/report/list";
    }

    @GetMapping("/{reportId}") // 특정 보고서 조회
    public String viewReport(@PathVariable("reportId") Long reportId, Model model) {
        Report report = reportService.getReportById(reportId);
        model.addAttribute("report", report);

//        if (report.getFileId() != null) {
//            FileMetadata reportFile = reportService.getReportFileById(report.getFileId());
//            model.addAttribute("reportFile", reportFile);
//        }
        return "/report/report-view";
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
    public String updateReport(@ModelAttribute Report report) {
        // 요청 수정 권한이 있는지 확인
        // 현재 로그인한 계정의 employeeId를 currentId로 설정
        String currentId = authService.getAuthenticatedUser().getUsername();

        // 요청 ID로 요청 조회
        Report reportForCheck = reportService.getReportById(report.getReportId());

        // 현재 로그인한 사용자와 requester_id 비교
        if (reportForCheck != null && reportForCheck.getWriterId().equals(currentId)) {
            // 작성자가 여러명이라면 현재 수정 중인 요청을 삭제하고 새로운 요청 생성
            if (report.getIdList().size() > 1) {
                reportService.deleteReport(report.getReportId());
                report.setWriterId(currentId);
                reportService.createReport(report);
            } else {
                reportService.updateReport(report);
            }
        } else {
            throw new SecurityException("권한이 없습니다.");
        }
        return "redirect:/report/list";
    }

    @DeleteMapping("/delete/{reportId}")
    public String deleteReport(@RequestParam("reportId") Long reportId) {
        // 보고서 삭제 권한이 있는지 확인
        // 현재 로그인한 계정의 employeeId를 currentId로 설정
        String currentId = authService.getAuthenticatedUser().getUsername();

        // 요청 ID로 요청 조회
        Report report = reportService.getReportById(reportId);

        // 현재 로그인한 사용자와 writer_id 비교
        if (report != null && report.getWriterId().equals(currentId)) {
            reportService.deleteReport(reportId);
        } else {
            throw new SecurityException("권한이 없습니다.");
        }
        return "redirect:/report/list";
    }
//=====================================================CRUD 메소드=======================================================
//=================================================날짜 설정 메소드========================================================
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
        return "redirect:/report/list";
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
        return "redirect:/report/requestList";
    }
//=================================================날짜 설정 메소드========================================================
//=================================================검색, 페이징 메소드=====================================================
    @GetMapping("/list") // 보고서 리스트 페이지
    public String showReportList(HttpSession session,
                                 @RequestParam(name = "page", defaultValue = "1") int page,
                                 @RequestParam(name = "size", defaultValue = "10") int size,
                                 @RequestParam(name = "keyword", defaultValue = "") String keyword,
                                 @RequestParam(name = "searchType", defaultValue = "1") int searchType,
                                 Model model) {
        // 로그인한 계정 기준 employee_id를 writerId(작성자)로 설정
        String writerId = authService.getAuthenticatedUser().getUsername();
        String reportStart = (String) session.getAttribute("staffReportStart");
        String reportEnd = (String) session.getAttribute("staffReportEnd");

        PageRequest pageRequest = new PageRequest(page - 1, size, keyword); // 페이지 번호 인덱싱을 위해 다시 -1
        PageResult<Report> pageResult = reportService.searchReports(pageRequest, writerId, searchType, reportStart, reportEnd);


        model.addAttribute("reports", pageResult.getData());
        model.addAttribute("currentPage", pageResult.getCurrentPage() + 1); // 뷰에서 가독성을 위해 +1
        model.addAttribute("totalPages", pageResult.getTotalPages());
        model.addAttribute("pageSize", size);
        model.addAttribute("keyword", keyword);
        model.addAttribute("searchType", searchType);

        return "report/report-list";
    }

    @GetMapping("/requestList") // 요청 리스트 페이지
    public String showRequestList(HttpSession session,
                                 @RequestParam(name = "page", defaultValue = "1") int page,
                                 @RequestParam(name = "size", defaultValue = "10") int size,
                                 @RequestParam(name = "keyword", defaultValue = "") String keyword,
                                 @RequestParam(name = "searchType", defaultValue = "1") int searchType,
                                 Model model) {
        // 로그인한 계정 기준 employee_id를 writerId(작성자)로 설정
        String writerId = authService.getAuthenticatedUser().getUsername();
        String requestStart = (String) session.getAttribute("staffRequestStart");
        String requestEnd = (String) session.getAttribute("staffRequestEnd");

        PageRequest pageRequest = new PageRequest(page - 1, size, keyword); // 페이지 번호 인덱싱을 위해 다시 -1
        PageResult<Request> pageResult = requestService.searchRequests(pageRequest, writerId, searchType, requestStart, requestEnd);

        model.addAttribute("requests", pageResult.getData());
        model.addAttribute("currentPage", pageResult.getCurrentPage() + 1); // 뷰에서 가독성을 위해 +1
        model.addAttribute("totalPages", pageResult.getTotalPages());
        model.addAttribute("pageSize", size);
        model.addAttribute("keyword", keyword);
        model.addAttribute("searchType", searchType);

        return "report/request-list";
    }
//=================================================검색, 페이징 메소드=====================================================
//=====================================================통계 메소드========================================================
    @GetMapping("/statistic") // 통계 날짜설정 페이지 이동
    public String showStatisticPage() {
        return "report/statistic";
    }

    @GetMapping("/stats") // 통계 날짜 설정
    public String getReportStats(@RequestParam(name = "startDate") String statisticStart,
                                 @RequestParam(name = "endDate") String statisticEnd,
                                 HttpSession session) {
        // 날짜 설정
        session.setAttribute("staffStatisticStart", statisticStart);
        session.setAttribute("staffStatisticEnd", statisticEnd);

        return "redirect:/report/main";
    }
//=====================================================통계 메소드========================================================

}
