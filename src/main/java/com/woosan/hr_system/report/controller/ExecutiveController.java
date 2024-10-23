package com.woosan.hr_system.report.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.woosan.hr_system.aspect.RequireManagerPermission;
import com.woosan.hr_system.auth.model.UserSessionInfo;
import com.woosan.hr_system.employee.dao.EmployeeDAO;
import com.woosan.hr_system.employee.model.Employee;
import com.woosan.hr_system.file.model.File;
import com.woosan.hr_system.file.service.FileService;
import com.woosan.hr_system.report.model.Report;
import com.woosan.hr_system.report.model.ReportStat;
import com.woosan.hr_system.report.model.Request;
import com.woosan.hr_system.report.service.ReportFileService;
import com.woosan.hr_system.report.service.ReportService;
import com.woosan.hr_system.report.service.RequestService;
import com.woosan.hr_system.search.PageRequest;
import com.woosan.hr_system.search.PageResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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

    // 결재 및 요청 현황
    @RequireManagerPermission
    @GetMapping("/dashboard")
    public String getMainPage(@RequestParam(name = "searchDate", required = false) String searchDate,
                              @RequestParam(name = "writerId", required = false) String writerId,
                              @RequestParam(name = "startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                              @RequestParam(name = "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                              Model model) throws JsonProcessingException {
        // 로그인한 계정 기준 employee_id를 approvalId(결재자)와 requestId(요청자)로 설정
        UserSessionInfo userSessionInfo = new UserSessionInfo();
        String employeeId = userSessionInfo.getCurrentEmployeeId();

        // 내가 결재할 보고서 목록 조회
        List<Report> reports = reportService.getUnprocessedReports(employeeId);

        // 내가 쓴 요청 목록 조회
        List<Request> requests = requestService.getMyRequests(employeeId);

        model.addAttribute("reports", reports);
        model.addAttribute("requests", requests);

        List<ReportStat> stats = reportService.getReportStats(startDate, endDate, writerId);

//        // 선택된 임원 목록 조회
//        List<Employee> selectedWriters = new ArrayList<>();
//        if (writerIdList != null && !writerIdList.isEmpty()) {
//            for (String writerId : writerIdList) {
//                Employee employee = employeeDAO.getEmployeeById(writerId);
//                selectedWriters.add(employee);
//            }
//            model.addAttribute("selectedWriters", selectedWriters);
//        }

        // 통계 View 관련 로직
        List<Object[]> statsArray = new ArrayList<>(); // JSON 변환
        statsArray.add(new Object[]{"월 별 보고서 통계", "총 보고서", "결재된 보고서", "결재 대기 보고서"});
        for (ReportStat stat : stats) {
            statsArray.add(new Object[]{stat.getMonth(), stat.getTotal(), stat.getFinished(), stat.getUnfinished()});
        }
        String statsJson = objectMapper.writeValueAsString(statsArray);
        model.addAttribute("statsJson", statsJson);

        model.addAttribute("writerId", writerId);
        model.addAttribute("writerName", employeeDAO.getEmployeeName(writerId));

        model.addAttribute("searchDate", searchDate);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);

        return "admin/report/approval-and-request";
    }
//=====================================================생성 메소드========================================================
    @RequireManagerPermission
    @GetMapping("/write") // 요청 생성 페이지 이동
    public String showWritePage(Model model) {
        List<Employee> employee = employeeDAO.getAllEmployees();
        model.addAttribute("request", new Request());
        model.addAttribute("employee", employee); // employees 목록 추가
        return "admin/report/write";
    }

    // 요청 생성
    @RequireManagerPermission
    @PostMapping("/write")
    public ResponseEntity<String> createRequest(@RequestBody Request request) {
        // 현재 로그인한 계정의 employeeId를 요청자(requesterId)로 설정
        UserSessionInfo userSessionInfo = new UserSessionInfo();
        String requesterId = userSessionInfo.getCurrentEmployeeId();
        request.setRequesterId(requesterId);
        requestService.createRequest(request);
        return ResponseEntity.ok("요청 작성이 완료되었습니다.");
    }
//=====================================================생성 메소드========================================================

//=====================================================조회 메소드========================================================
    // 내가 작성한 요청 리스트
    @GetMapping("/list")
    public String showRequestList(@RequestParam(name = "page", defaultValue = "1") int page,
                                  @RequestParam(name = "size", defaultValue = "10") int size,
                                  @RequestParam(name = "keyword", defaultValue = "") String keyword,
                                  @RequestParam(name = "searchType", defaultValue = "0") int searchType,
                                  @RequestParam(name = "searchDate", defaultValue = "") String searchDate,
                                  @RequestParam(name = "startDate", defaultValue = "") String startDate,
                                  @RequestParam(name = "endDate", defaultValue = "") String endDate,
                                 Model model) {
        // 로그인한 계정 기준 employee_id를 writerId(작성자)로 설정
        UserSessionInfo userSessionInfo = new UserSessionInfo();
        String requesterId = userSessionInfo.getCurrentEmployeeId();

        PageRequest pageRequest = new PageRequest(page - 1, size, keyword); // 페이지 번호 인덱싱을 위해 다시 -1
        PageResult<Request> pageResult = requestService.searchMyRequests(pageRequest, requesterId, searchType, startDate, endDate);

        model.addAttribute("requests", pageResult.getData());
        model.addAttribute("currentPage", pageResult.getCurrentPage() + 1); // 뷰에서 가독성을 위해 +1
        model.addAttribute("totalPages", pageResult.getTotalPages());
        model.addAttribute("pageSize", size);
        model.addAttribute("keyword", keyword);
        model.addAttribute("searchType", searchType);
        model.addAttribute("searchDate", searchDate);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);

        return "/admin/report/request/list";
    }

    // 내가 결재할 보고서 목록
    @GetMapping("/approval")
    public String showReportList(@RequestParam(name = "page", defaultValue = "1") int page,
                                 @RequestParam(name = "size", defaultValue = "10") int size,
                                 @RequestParam(name = "keyword", defaultValue = "") String keyword,
                                 @RequestParam(name = "searchType", defaultValue = "0") int searchType,
                                 @RequestParam(name = "approvalStatus", defaultValue = "") String approvalStatus,
                                 @RequestParam(name = "searchDate", defaultValue = "") String searchDate,
                                 @RequestParam(name = "startDate", defaultValue = "") String startDate,
                                 @RequestParam(name = "endDate", defaultValue = "") String endDate,
                                 Model model) {
        // 로그인한 계정 기준 employee_id를 approverId(작성자)로 설정
        UserSessionInfo userSessionInfo = new UserSessionInfo();
        String approverId = userSessionInfo.getCurrentEmployeeId();

        PageRequest pageRequest = new PageRequest(page - 1, size, keyword); // 페이지 번호 인덱싱을 위해 다시 -1
        PageResult<Report> pageResult = reportService.toApproveSearchReports(pageRequest, approverId, searchType, approvalStatus, startDate, endDate);

        model.addAttribute("reports", pageResult.getData());
        model.addAttribute("currentPage", pageResult.getCurrentPage() + 1); // 뷰에서 가독성을 위해 +1
        model.addAttribute("totalPages", pageResult.getTotalPages());
        model.addAttribute("pageSize", size);
        model.addAttribute("keyword", keyword);
        model.addAttribute("searchType", searchType);
        model.addAttribute("searchDate", searchDate);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("approvalStatus", approvalStatus);

        return "admin/report/approval";
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

    // 통계 - 선택된 임원 목록 중 삭제될 시 실행
    @ResponseBody
    @RequireManagerPermission
    @PostMapping("/updateStats")
    public Map<String, Object> updateStats(@RequestBody Map<String, Object> payload)
                                            throws JsonProcessingException {
        List<String> writerIdList = (List<String>) payload.get("idList");
        LocalDate startDate = null;
        LocalDate endDate = null;

        // startDate 및 endDate가 null이 아니면 파싱
        if (payload.get("startDate") != null) {
            startDate = LocalDate.parse((String) payload.get("startDate"));
        }
        if (payload.get("endDate") != null) {
            endDate = LocalDate.parse((String) payload.get("endDate"));
        }

        // 통계 데이터 조회
        List<ReportStat> stats = reportService.getReportStats(startDate, endDate, writerIdList);
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
    public ResponseEntity<String> updateRequest(@RequestBody Request request) {
        // ↓ 요청 수정 권한이 있는지 확인 ↓
        // 현재 로그인한 계정의 employeeId를 currentId로 설정
        UserSessionInfo userSessionInfo = new UserSessionInfo();
        String currentId = userSessionInfo.getCurrentEmployeeId();

        // 현재 로그인한 사용자와 requester_id 비교
        if (request.getRequesterId() != null && request.getRequesterId().equals(currentId)) {
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
        return ResponseEntity.ok("요청 수정이 완료되었습니다.");
    }

    @RequireManagerPermission
    @PostMapping("/approve") // 보고서 결재 처리
    public String approveReport(@RequestParam("reportId") int reportId,
                                @RequestParam("status") String status,
                                @RequestParam(name = "rejectionReason", required = false) String rejectionReason) {
        try {
            reportService.updateApprovalStatus(reportId, status, rejectionReason);
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
