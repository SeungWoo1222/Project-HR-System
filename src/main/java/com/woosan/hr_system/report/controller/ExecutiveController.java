package com.woosan.hr_system.report.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.woosan.hr_system.aspect.RequireManagerPermission;
import com.woosan.hr_system.auth.model.UserSessionInfo;
import com.woosan.hr_system.employee.dao.EmployeeDAO;
import com.woosan.hr_system.employee.model.Employee;
import com.woosan.hr_system.report.model.Report;
import com.woosan.hr_system.report.model.ReportStat;
import com.woosan.hr_system.report.model.Request;
import com.woosan.hr_system.report.service.ReportFileService;
import com.woosan.hr_system.report.service.ReportService;
import com.woosan.hr_system.report.service.RequestService;
import com.woosan.hr_system.search.PageRequest;
import com.woosan.hr_system.search.PageResult;
import com.woosan.hr_system.upload.model.File;
import com.woosan.hr_system.upload.service.FileService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
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
    private FileService fileService;
    @Autowired
    private ReportFileService reportFileService;

    // main 페이지
    @RequireManagerPermission
    @GetMapping("/main")
    public String getMainPage(HttpSession session, Model model) throws JsonProcessingException {

        // 로그인한 계정 기준 employee_id를 approvalId(결재자)와 requestId(요청자)로 설정
        UserSessionInfo userSessionInfo = new UserSessionInfo();
        String employeeId = userSessionInfo.getCurrentEmployeeId();

        // 내가 결재할 보고서 목록 조회
        List<Report> reports = reportService.getUnprocessedReports(employeeId);

        // 내가 쓴 요청 목록 조회
        List<Request> requests = requestService.getMyRequests(employeeId);

        model.addAttribute("reports", reports);
        model.addAttribute("requests", requests);

        // 보고서 통계
        String statisticStart = (String) session.getAttribute("statisticStart");
        String statisticEnd = (String) session.getAttribute("statisticEnd");
        List<String> writerIdList = (List<String>) session.getAttribute("idList");

        List<ReportStat> stats = reportService.getReportStats(statisticStart, statisticEnd, writerIdList);

        // 선택된 임원 목록 조회
        List<Employee> selectedWriters = new ArrayList<>();
        if (writerIdList != null && !writerIdList.isEmpty()) {
            for (String writerId : writerIdList) {
                Employee employee = employeeDAO.getEmployeeById(writerId);
                selectedWriters.add(employee);
            }
            model.addAttribute("selectedWriters", selectedWriters);
        }

        // 통계 View 관련 로직
        List<Object[]> statsArray = new ArrayList<>(); // JSON 변환
        statsArray.add(new Object[]{"월 별 보고서 통계", "총 보고서", "결재 된 보고서", "결재 대기인 보고서"});
        for (ReportStat stat : stats) {
            statsArray.add(new Object[]{stat.getMonth(), stat.getTotal(), stat.getFinished(), stat.getUnfinished()});
        }
        String statsJson = objectMapper.writeValueAsString(statsArray);
        model.addAttribute("statsJson", statsJson);

        return "admin/report/main";
    }
//=====================================================생성 메소드========================================================
    @RequireManagerPermission
    @GetMapping("/write") // 요청 생성 페이지 이동
    public String showWritePage(Model model) {
        List<Employee> employee = employeeDAO.getAllEmployees();
        model.addAttribute("request", new Request());
        model.addAttribute("employee", employee); // employees 목록 추가
        return "admin/report/request-write";
    }

    // 요청 생성
    @RequireManagerPermission
    @PostMapping("/write")
    public String createRequest(@ModelAttribute Request request) {
        // 현재 로그인한 계정의 employeeId를 요청자(requesterId)로 설정
        UserSessionInfo userSessionInfo = new UserSessionInfo();
        String requesterId = userSessionInfo.getCurrentEmployeeId();
        request.setRequesterId(requesterId);
        requestService.createRequest(request);
        return "redirect:/admin/request/requestList";
    }
//=====================================================생성 메소드========================================================

//=====================================================조회 메소드========================================================
    @GetMapping("/requestList")
    public String showRequestList(HttpSession session,
                                 @RequestParam(name = "page", defaultValue = "1") int page,
                                 @RequestParam(name = "size", defaultValue = "10") int size,
                                 @RequestParam(name = "keyword", defaultValue = "") String keyword,
                                 @RequestParam(name = "searchType", defaultValue = "1") int searchType,
                                 Model model) {
        // 로그인한 계정 기준 employee_id를 writerId(작성자)로 설정
        UserSessionInfo userSessionInfo = new UserSessionInfo();
        String requesterId = userSessionInfo.getCurrentEmployeeId();

        // 설정된 조회 기간을 가져옴(없다면 현재 달에 쓰인 보고서를 보여줌)
        String requestStart = (String) session.getAttribute("requestStart");
        String requestEnd = (String) session.getAttribute("requestEnd");

        PageRequest pageRequest = new PageRequest(page - 1, size, keyword); // 페이지 번호 인덱싱을 위해 다시 -1
        PageResult<Request> pageResult = requestService.searchMyRequests(pageRequest, requesterId, searchType, requestStart, requestEnd);


        model.addAttribute("requests", pageResult.getData());
        model.addAttribute("currentPage", pageResult.getCurrentPage() + 1); // 뷰에서 가독성을 위해 +1
        model.addAttribute("totalPages", pageResult.getTotalPages());
        model.addAttribute("pageSize", size);
        model.addAttribute("keyword", keyword);
        model.addAttribute("searchType", searchType);

        return "/admin/report/request-list";
    }

    @GetMapping("toApproveReportList")
    public String showReportList(HttpSession session,
                                 @RequestParam(name = "page", defaultValue = "1") int page,
                                 @RequestParam(name = "size", defaultValue = "10") int size,
                                 @RequestParam(name = "keyword", defaultValue = "") String keyword,
                                 @RequestParam(name = "searchType", defaultValue = "1") int searchType,
                                 Model model) {
        // 로그인한 계정 기준 employee_id를 writerId(작성자)로 설정
        UserSessionInfo userSessionInfo = new UserSessionInfo();
        String approverId = userSessionInfo.getCurrentEmployeeId();

        // 설정된 조회 기간을 가져옴(없다면 현재 달에 쓰인 보고서를 보여줌)
        String reportStart = (String) session.getAttribute("approvalStart");
        String reportEnd = (String) session.getAttribute("approvalEnd");

        PageRequest pageRequest = new PageRequest(page - 1, size, keyword); // 페이지 번호 인덱싱을 위해 다시 -1
        PageResult<Report> pageResult = reportService.toApproveSearchReports(pageRequest, approverId, searchType, reportStart, reportEnd);


        model.addAttribute("reports", pageResult.getData());
        model.addAttribute("currentPage", pageResult.getCurrentPage() + 1); // 뷰에서 가독성을 위해 +1
        model.addAttribute("totalPages", pageResult.getTotalPages());
        model.addAttribute("pageSize", size);
        model.addAttribute("keyword", keyword);
        model.addAttribute("searchType", searchType);

        return "admin/report/report-list";
    }

    @GetMapping("/{requestId}") // 요청 세부 조회
    public String viewRequest(@PathVariable("requestId") int requestId, Model model) {
        Request request = requestService.getRequestById(requestId);
        model.addAttribute("request", request);
        return "admin/report/request-view";
    }

    @GetMapping("/report/{reportId}") // 특정 보고서 조회
    public String viewReport(@PathVariable("reportId") int reportId, Model model) {
        Report report = reportService.getReportById(reportId);
        model.addAttribute("report", report);

        List<Integer> fileIds = reportFileService.getFileIdsByReportId(reportId);
        // 보고서에 맞는 파일이 있다면 실행
        if (!fileIds.isEmpty()) {
            List<File> files = fileService.getFileListById(fileIds);
            model.addAttribute("files", files);
        }

        return "admin/report/report-view";
    }

    @GetMapping("/statistic") // 통계 날짜, 임원 설정 페이지 이동
    public String showStatisticPage(Model model) {
        List<Employee> employees = employeeDAO.getAllEmployees();
        model.addAttribute("employees", employees); // employees 목록 추가
        return "/admin/report/statistic";
    }

    @RequireManagerPermission
    @PostMapping("/stats") // 통계 날짜, 임원 설정
    public String getReportStats(@RequestParam(name = "statisticStart") String statisticStart,
                                 @RequestParam(name = "statisticEnd") String statisticEnd,
                                 @RequestParam(required = false, name = "idList") List<String> idList,
                                 HttpSession session) {

        System.out.println("idList" + idList);

        // 날짜 설정
        session.setAttribute("statisticStart", statisticStart);
        session.setAttribute("statisticEnd", statisticEnd);

        // 임원 설정
        session.setAttribute("idList", idList);

        return "redirect:/admin/request/main";
    }

    // 통계 - 선택된 임원 목록 중 삭제될 시 실행
    @ResponseBody
    @RequireManagerPermission
    @PostMapping("/updateStats")
    public Map<String, Object> updateStats(HttpSession session, @RequestBody List<String> ids) throws JsonProcessingException {
        String statisticStart = (String) session.getAttribute("statisticStart");
        String statisticEnd = (String) session.getAttribute("statisticEnd");

        // 삭제된 임원 외 임원들을 다시 session에 등록
        session.setAttribute("idList", ids);

        if (ids.isEmpty()) {
            session.removeAttribute("idList");
        }

        // 통계 데이터 조회
        List<ReportStat> stats = reportService.getReportStats(statisticStart, statisticEnd, ids);
        return prepareStatsResponse(stats);
    }

    // 임원 삭제 후 main.html에 통계를 다시 갱신하는 매소드
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
    @RequireManagerPermission
    @GetMapping("/approvalDatePage")
    public String showApprovalDatePage() {
        return "admin/report/report-approval-date";
    }

    // 내 결재 목록 조회 + 날짜 범위 설정
    @RequireManagerPermission
    @GetMapping("/approvalDate")
    public String setApprovalListDateRange(@RequestParam(name = "approvalStart") String approvalStart,
                                           @RequestParam(name = "approvalEnd") String approvalEnd,
                                           HttpSession session) {
        session.setAttribute("approvalStart", approvalStart);
        session.setAttribute("approvalEnd", approvalEnd);
        return "redirect:/admin/request/toApproveReportList";
    }

    // 내가 쓴 작성 요청목록 날짜 범위설정 페이지 이동
    @RequireManagerPermission
    @GetMapping("/requestDatePage")
    public String showRequestDatePage() {
        return "admin/report/request-date";
    }

    // 내 결재 목록 조회 + 날짜 범위 설정
    @RequireManagerPermission
    @GetMapping("/requestDate")
    public String setRequestListDateRange(@RequestParam(name = "requestStart") String requestStart,
                                          @RequestParam(name = "requestEnd") String requestEnd,
                                          HttpSession session) {
        session.setAttribute("requestStart", requestStart);
        session.setAttribute("requestEnd", requestEnd);
        return "redirect:/admin/request/requestList";
    }



//====================================================조회 메소드========================================================
//====================================================수정 메소드========================================================
    @RequireManagerPermission
    @GetMapping("/edit") // 요청 수정 페이지 이동
    public String showUpdateRequestPage(@RequestParam(name = "requestId") int requestId, Model model) {
        Request request = requestService.getRequestById(requestId);
        List<Employee> employees = employeeDAO.getAllEmployees();
        model.addAttribute("employees", employees); // employees 목록 추가
        model.addAttribute("request", request);

        model.addAttribute("updateRequest", new Request());
        return "admin/report/request-edit";
    }

    @RequireManagerPermission
    @PostMapping("/edit") // 요청 수정
    public String updateRequest(@ModelAttribute Request request) {
        // 요청 수정 권한이 있는지 확인
        // 현재 로그인한 계정의 employeeId를 currentId로 설정
        UserSessionInfo userSessionInfo = new UserSessionInfo();
        String currentId = userSessionInfo.getCurrentEmployeeId();

        // 요청 ID로 요청 조회
        Request requestForCheck = requestService.getRequestById(request.getRequestId());

        // 현재 로그인한 사용자와 requester_id 비교
        if (requestForCheck != null && requestForCheck.getRequesterId().equals(currentId)) {
            // 작성자가 여러명이라면 현재 수정 중인 요청을 삭제하고 새로운 요청 생성
            if (request.getIdList().size() > 1) {
                requestService.deleteRequest(request.getRequestId());
                request.setRequesterId(currentId);
                requestService.createRequest(request);
            } else {
                requestService.updateRequest(request);
            }
        } else {
            throw new SecurityException("권한이 없습니다.");
        }

        return "redirect:/admin/request/requestList";
    }

    @RequireManagerPermission
    @PostMapping("/approve") // 보고서 결재 처리
    public String approveReport(@RequestParam("reportId") int reportId,
                                @RequestParam("status") String status,
                                @RequestParam(name = "rejectionReason", required = false) String rejectionReason) {
        try {
            requestService.updateApprovalStatus(reportId, status, rejectionReason);
            return "redirect:/admin/request/toApproveReportList";
        } catch (Exception e) {
            return "error"; // 에러 메시지 표시
        }
    }
//===================================================수정 메소드=========================================================

//===================================================삭제 메소드=========================================================

    @RequireManagerPermission
    @DeleteMapping("/delete/{requestId}") // 요청 삭제
    public String deleteRequest(@PathVariable("requestId") int requestId) {
        // 요청 삭제 권한이 있는지 확인

        // 현재 로그인한 계정의 employeeId를 currentId로 설정
        UserSessionInfo userSessionInfo = new UserSessionInfo();
        String currentId = userSessionInfo.getCurrentEmployeeId();

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

//===================================================삭제 메소드=========================================================

}
