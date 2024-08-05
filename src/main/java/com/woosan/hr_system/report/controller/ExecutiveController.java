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
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    @Autowired
    private AuthService authService;
    @Qualifier("forceAutoProxyCreatorToUseClassProxying")
    @Autowired
    private BeanFactoryPostProcessor forceAutoProxyCreatorToUseClassProxying;


    @GetMapping("/write") // 요청 생성 페이지 이동
    public String showWritePage(Model model) {
        List<Employee> employee = employeeDAO.getAllEmployees();
        model.addAttribute("request", new Request());
        model.addAttribute("employee", employee); // employees 목록 추가
        return "admin/report/request-write";
    }

    // 요청 생성
    @PostMapping("/write")
    public String createRequest(@ModelAttribute Request request) {
        // 현재 로그인한 계정의 employeeId를 요청자(requesterId)로 설정
        String requesterId = authService.getAuthenticatedUser().getUsername();
        request.setRequesterId(requesterId);
        requestService.createRequest(request);
        return "redirect:/admin/request/main";
    }

    // main 페이지
    @GetMapping("/main")
    public String getMainPage(HttpSession session, Model model) throws JsonProcessingException {

        // 로그인한 계정 기준 employee_id를 approvalId(결재자)와 requestId(요청자)로 설정
        String employeeId = authService.getAuthenticatedUser().getUsername();

        // 내가 결재할 보고서 목록 조회
        String approvalStart = (String) session.getAttribute("approvalStart");
        String approvalEnd = (String) session.getAttribute("approvalEnd");
        List<Report> reports = reportService.getPendingApprovalReports(employeeId, approvalStart, approvalEnd);

        // 내가 쓴 요청 목록 조회
        String requestStart = (String) session.getAttribute("requestStart");
        String requestEnd = (String) session.getAttribute("requestEnd");
        List<Request> requests = requestService.getMyRequests(employeeId, requestStart ,requestEnd);

        model.addAttribute("reports", reports);
        model.addAttribute("requests", requests);

        // 보고서 통계
        String statisticStart = (String) session.getAttribute("statisticStart");
        String statisticEnd = (String) session.getAttribute("statisticEnd");
        List<String> writerIds = (List<String>) session.getAttribute("writerIds");
        List<ReportStat> stats = reportService.getReportStats(statisticStart, statisticEnd, writerIds);

        // 선택된 임원 목록 조회
        List<Employee> selectedWriters = new ArrayList<>();
        if (writerIds != null && !writerIds.isEmpty()) {
            for (String writerId : writerIds) {
                Employee employee = employeeDAO.getEmployeeById(writerId);
                selectedWriters.add(employee);
            }
            model.addAttribute("selectedWriters", selectedWriters);
            model.addAttribute("allWritersSelected", false);
        } else {
            model.addAttribute("allWritersSelected", true);
        }

        // 통계 View 관련 로직
        List<Object[]> statsArray = new ArrayList<>(); // JSON 변환
        statsArray.add(new Object[]{"월 별 보고서 통계", "총 보고서", "결재 된 보고서", "결재 대기인 보고서"});
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
        model.addAttribute("employees", employees); // employees 목록 추가
        return "/admin/report/statistic";
    }

    @GetMapping("/stats") // 통계 날짜, 임원 설정
    public String getReportStats(@RequestParam(name = "statisticStart") String statisticStart,
                                 @RequestParam(name = "statisticEnd") String statisticEnd,
                                 @RequestParam(required = false, name = "writerId") List<String> writerIds,
                                 HttpSession session) {

        // 날짜 설정
        session.setAttribute("statisticStart", statisticStart);
        session.setAttribute("statisticEnd", statisticEnd);

        // 임원 설정
        session.setAttribute("writerIds", writerIds);

        return "redirect:/admin/request/main";
    }

    // 통계 - 선택된 임원 목록 중 삭제될 시 실행
    @PostMapping("/updateStats")
    @ResponseBody
    public Map<String, Object> updateStats(HttpSession session, @RequestBody List<String> writerIds) throws JsonProcessingException {
        String statisticStart = (String) session.getAttribute("statisticStart");
        String statisticEnd = (String) session.getAttribute("statisticEnd");

        // 통계 데이터 조회
        List<ReportStat> stats = reportService.getReportStats(statisticStart, statisticEnd, writerIds);
        return prepareStatsResponse(stats);
    }

    // main.html에 통계를 다시 갱신하는 매소드(임원 삭제 후)
    private Map<String, Object> prepareStatsResponse(List<ReportStat> stats) throws JsonProcessingException {
        List<Object[]> statsArray = new ArrayList<>();
        statsArray.add(new Object[]{"월 별 보고서 통계", "총 보고서", "결재 된 보고서", "결재 대기인 보고서"});
        for (ReportStat stat : stats) {
            statsArray.add(new Object[]{stat.getMonth(), stat.getTotal(), stat.getFinished(), stat.getUnfinished()});
        }
        String statsJson = objectMapper.writeValueAsString(statsArray);

        Map<String, Object> response = new HashMap<>();
        response.put("statsJson", statsJson);
        return response;
    }

    // 내 결재 목록 날짜 범위설정 페이지 이동
    @GetMapping("/approvalDatePage")
    public String showApprovalDatePage() {
        return "admin/report/report-approval-date";
    }

    // 내 결재 목록 조회 + 날짜 범위 설정
    @GetMapping("/approvalDate")
    public String setApprovalListDateRange(@RequestParam(name = "approvalStart") String approvalStart,
                                           @RequestParam(name = "approvalEnd") String approvalEnd,
                                           HttpSession session) {
        session.setAttribute("approvalStart", approvalStart);
        session.setAttribute("approvalEnd", approvalEnd);
        return "redirect:/admin/request/main";
    }

    // 내가 쓴 작성 요청목록 날짜 범위설정 페이지 이동
    @GetMapping("/requestDatePage")
    public String showRequestDatePage() {
        return "admin/report/request-date";
    }

    // 내 결재 목록 조회 + 날짜 범위 설정
    @GetMapping("/requestDate")
    public String setRequestListDateRange(@RequestParam(name = "requestStart") String requestStart,
                                          @RequestParam(name = "requestEnd") String requestEnd,
                                          HttpSession session) {
        session.setAttribute("requestStart", requestStart);
        session.setAttribute("requestEnd", requestEnd);
        return "redirect:/admin/request/main";
    }

    @GetMapping("/{requestId}") // 특정 요청 조회
    public String viewRequest(@PathVariable("requestId") Long requestId, Model model) {
        Request request = requestService.getRequestById(requestId);
        model.addAttribute("request", request);
        return "admin/report/request-view";
    }


    @GetMapping("/edit") // 요청 수정 페이지 이동
    public String editRequest(@RequestParam(name = "requestId") Long requestId, Model model) {
        Request request = requestService.getRequestById(requestId);
        List<Employee> employees = employeeDAO.getAllEmployees();
        model.addAttribute("employees", employees); // employees 목록 추가
        model.addAttribute("request", request);

        model.addAttribute("updateRequest", new Request());
        return "admin/report/request-edit";
    }

    @PostMapping("/edit") // 요청 수정
    public String updateRequest(@ModelAttribute Request request) {
        System.out.println(request.getRequestNote());
        // 요청 수정 권한이 있는지 확인
        // 현재 로그인한 계정의 employeeId를 currentId로 설정
        String currentId = authService.getAuthenticatedUser().getUsername();

        // 요청 ID로 요청 조회
        Request requestForCheck = requestService.getRequestById(request.getRequestId());

        // 현재 로그인한 사용자와 requester_id 비교
        if (requestForCheck != null && requestForCheck.getRequesterId().equals(currentId)) {
            // 작성자가 여러명이라면 현재 수정 중인 요청을 삭제하고 새로운 요청 생성
            if (request.getWriterIdList().size() > 1) {
                requestService.deleteRequest(request.getRequestId());
                request.setRequesterId(currentId);
                requestService.createRequest(request);
            } else {
                requestService.updateRequest(request);
            }
        } else {
            throw new SecurityException("권한이 없습니다.");
        }

        return "redirect:/admin/request/main";
    }

    @DeleteMapping("/delete/{requestId}") // 요청 삭제
    public String deleteRequest(@PathVariable("requestId") Long requestId) {
        // 요청 삭제 권한이 있는지 확인

        // 현재 로그인한 계정의 employeeId를 currentId로 설정
        String currentId = authService.getAuthenticatedUser().getUsername();

        // 요청 ID로 요청 조회
        Request request = requestService.getRequestById(requestId);

        // 현재 로그인한 사용자와 requester_id 비교
        if (request != null && request.getRequesterId().equals(currentId)) {
            requestService.deleteRequest(requestId);
        } else {
            throw new SecurityException("권한이 없습니다.");
        }
        return "redirect:/admin/request/main";
    }


    // 요청 외에 맵핑 메소드들 ↓↓↓

    @GetMapping("/report/{reportId}") // 특정 보고서 조회
    public String viewReport(@PathVariable("reportId") Long reportId, Model model) {
        Report report = reportService.getReportById(reportId);
        model.addAttribute("report", report);

        if (report.getFileId() != null) {
            FileMetadata reportFile = reportService.getReportFileById(report.getFileId());
            model.addAttribute("reportFile", reportFile);
        }
        return "admin/report/report-view";
    }

    @PostMapping("/approve") // 보고서 결재 처리
    public String approveReport(@RequestParam("reportId") Long reportId,
                                @RequestParam("status") String status,
                                @RequestParam(name = "rejectionReason", required = false) String rejectionReason) {
        try {
            requestService.updateApprovalStatus(reportId, status, rejectionReason);
            return "redirect:/admin/request/main";
        } catch (Exception e) {
            return "error"; // 에러 메시지 표시
        }
    }

    @GetMapping("/employee") // 부서 기반 임원 정보 조회
    @ResponseBody
    public List<Employee> getEmployeesByDepartment(@RequestParam("departmentId") String departmentId) {
        List<Employee> employees = employeeDAO.getEmployeesByDepartment(departmentId);
        if (employees != null) {
            for (Employee employee : employees) {
                if (employee == null) {
                    System.out.println("Employee is null");
                } else {
                    System.out.println(employee);
                }
            }
        } else {
            System.out.println("Employees list is null");
        }
        return employees;
    }


}
