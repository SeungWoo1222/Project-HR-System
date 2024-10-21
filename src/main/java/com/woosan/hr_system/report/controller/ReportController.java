package com.woosan.hr_system.report.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import com.woosan.hr_system.schedule.model.Schedule;
import com.woosan.hr_system.schedule.service.ScheduleService;
import com.woosan.hr_system.search.PageRequest;
import com.woosan.hr_system.search.PageResult;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/report")
public class ReportController {
    @Autowired
    private ReportService reportService;
    @Autowired
    private RequestService requestService;
    @Autowired
    private FileService fileService;
    @Autowired
    private ScheduleService scheduleService;
    @Autowired
    private EmployeeDAO employeeDAO;
    @Autowired
    private ObjectMapper objectMapper; // 통계 반환 후 view에 보내면서 JSON로 반환함
    @Autowired
    private ReportFileService reportFileService;

    @GetMapping("/my") // main 페이지 이동
    public String getMainPage(@RequestParam(name = "searchDate", required = false) String searchDate,
                              @RequestParam(name = "startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                              @RequestParam(name = "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                              Model model) throws JsonProcessingException {
        // 로그인한 계정 기준 employee_id를 writerId(작성자)로 설정
        UserSessionInfo userSessionInfo = new UserSessionInfo();
        String writerId = userSessionInfo.getCurrentEmployeeId();

        // 내가 쓴 보고서 조회(최근 5개)
        List<Report> reports = reportService.getRecentReports(writerId);
        model.addAttribute("reports", reports);

        // 내게 온 요청 조회(최근 5개 요청)
        List<Request> requests = requestService.getMyPendingRequests(writerId);
        model.addAttribute("requests", requests);

        List<String> employeeIds = Collections.singletonList(writerId); // writerId를 List<String>으로 변환 후 전달
        List<ReportStat> stats = reportService.getReportStats(startDate, endDate, employeeIds);

        // 통계 View 관련 로직
        List<Object[]> statsArray = new ArrayList<>(); // JSON 변환
        statsArray.add(new Object[]{"월 별 보고서 통계", "총 보고서 수", "완료된 보고서 수", "미완료된 보고서 수"});
        for (ReportStat stat : stats) {
            statsArray.add(new Object[]{stat.getMonth(), stat.getTotal(), stat.getFinished(), stat.getUnfinished()});
        }
        String statsJson = objectMapper.writeValueAsString(statsArray);
        model.addAttribute("statsJson", statsJson);

        model.addAttribute("searchDate", searchDate);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        return "report/my";
    }

    //=====================================================생성 메소드======================================================
    @GetMapping("/showCreatePage") // 보고서 생성 페이지 이동
    public String showCreatePage(Model model) {
        List<Employee> employees = employeeDAO.getAllEmployees();
        model.addAttribute("employees", employees); // employees 목록 추가
        model.addAttribute("report", new Report());
        return "report/write";
    }

    // 보고서 생성
    @ResponseBody
    @PostMapping(value = "/write", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> createReport(@RequestPart(value="report") Report report,
                               @RequestPart(value="reportFiles", required=false) List<MultipartFile> reportDocuments) {

        UserSessionInfo userSessionInfo = new UserSessionInfo();
        String writerId = userSessionInfo.getCurrentEmployeeId();
        LocalDateTime currentTime = userSessionInfo.getNow();
        report.setWriterId(writerId);
        report.setCreatedDate(currentTime);

        if (reportDocuments == null || reportDocuments.isEmpty()) {
            reportService.createReport(report);
        } else {
            reportService.createReportWithFile(report, reportDocuments);
        }
        return ResponseEntity.ok("보고서 작성이 완료되었습니다.");
    }

    @GetMapping("/writeFromRequest/{requestId}") // 요청 들어온 보고서 생성 페이지 이동
    public String showCreatePageFromRequest(@PathVariable("requestId") int requestId,
                                            Model model) {

        Request request = requestService.getRequestByWriter(requestId);
        model.addAttribute("request", request);
        model.addAttribute("report", new Report());
        return "/report/write-from-request";
    }

    // 요청 들어온 보고서 생성
    @ResponseBody
    @PostMapping(value = "/writeFromRequest", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> CreateReportFromRequest(
                                          @RequestPart(value="report") Report report,
                                          @RequestPart(value="reportFiles", required=false) List<MultipartFile> reportDocuments,
                                          @RequestParam(value="requestId") int requestId) {
        UserSessionInfo userSessionInfo = new UserSessionInfo();
        // 현재 로그인한 계정의 employeeId를 요청자(writer)로 설정
        String writerId = userSessionInfo.getCurrentEmployeeId();
        // 보고서 생성 시간 설정
        LocalDateTime currentTime = userSessionInfo.getNow();
        report.setWriterId(writerId);
        report.setCreatedDate(currentTime);

        if (reportDocuments == null || reportDocuments.isEmpty()) {
            int reportId = reportService.createReportFromRequest(report);
            requestService.updateReportId(requestId, reportId);
        } else {
            int reportId = reportService.createReportFromRequestWithFile(report, reportDocuments);
            requestService.updateReportId(requestId, reportId);
        }
        return ResponseEntity.ok("보고서 생성이 완료되었습니다.");
    }

    // 일정 완료 된 보고서 생성 페이지 이동
    @GetMapping("/writeFromSchedule/{taskId}")
    public String showCreatePageFromSchedule(@PathVariable("taskId") int taskId,
                                            Model model) {

        log.info("showCreatePageFromSchedule 도착 taskId : {}", taskId);

        Schedule schedule = scheduleService.getScheduleById(taskId);
        model.addAttribute("schedule", schedule);
        model.addAttribute("report", new Report());
        return "/report/write-from-schedule";
    }

    // 일정 완료 된 보고서 생성
//    @PostMapping("writeFromSchedule")
//    public ResponseEntity<String> createReportFromSchedule() {
//        return "보고서 생성이 완료되었습니다.";
//    }

//=================================================생성 메소드============================================================
//=================================================조회 메소드============================================================

    @GetMapping("/{reportId}") // 특정 보고서 조회
    public String viewReport(@PathVariable("reportId") int reportId, Model model) {
        Report report = reportService.getReportById(reportId);
        model.addAttribute("report", report);

        List<Integer> fileIds = reportFileService.getFileIdsByReportId(reportId);
        // 보고서에 맞는 파일이 있다면 실행
        if (!fileIds.isEmpty()) {
            List<File> files = fileService.getFileListById(fileIds);
            model.addAttribute("files", files);
        }

        return "/report/report-view";
    }

    // 내가 작성한 보고서 리스트
    @GetMapping("/list")
    public String showReportList(@RequestParam(name = "page", defaultValue = "1") int page,
                                 @RequestParam(name = "size", defaultValue = "10") int size,
                                 @RequestParam(name = "keyword", defaultValue = "") String keyword,
                                 @RequestParam(name = "searchType", defaultValue = "0") int searchType,
                                 @RequestParam(name = "approvalStatus", defaultValue = "") String approvalStatus,
                                 @RequestParam(name = "searchDate", defaultValue = "") String searchDate,
                                 @RequestParam(name = "startDate", defaultValue = "") String startDate,
                                 @RequestParam(name = "endDate", defaultValue = "") String endDate,
                                 Model model) {
        // 내가 쓴 보고서를 보기위해 writerId를 전송
        UserSessionInfo userSessionInfo = new UserSessionInfo();
        String writerId = userSessionInfo.getCurrentEmployeeId();

        PageRequest pageRequest = new PageRequest(page - 1, size, keyword); // 페이지 번호 인덱싱을 위해 다시 -1
        PageResult<Report> pageResult = reportService.searchReports(pageRequest, writerId, searchType, approvalStatus, startDate, endDate);

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

        return "report/list";
    }

    // 내게 온 요청 리스트
    @GetMapping("/requestList")
    public String showRequestList(@RequestParam(name = "page", defaultValue = "1") int page,
                                  @RequestParam(name = "size", defaultValue = "10") int size,
                                  @RequestParam(name = "keyword", defaultValue = "") String keyword,
                                  @RequestParam(name = "searchType", defaultValue = "1") int searchType,
                                  @RequestParam(name = "startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                  @RequestParam(name = "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                                  Model model) {

        // 로그인한 계정 기준 employee_id를 writerId(작성자)로 설정
        UserSessionInfo userSessionInfo = new UserSessionInfo();
        String writerId = userSessionInfo.getCurrentEmployeeId();

        PageRequest pageRequest = new PageRequest(page - 1, size, keyword); // 페이지 번호 인덱싱을 위해 다시 -1
        PageResult<Request> pageResult = requestService.searchRequests(pageRequest, writerId, searchType, startDate, endDate);


        model.addAttribute("requests", pageResult.getData());
        model.addAttribute("currentPage", pageResult.getCurrentPage() + 1); // 뷰에서 가독성을 위해 +1
        model.addAttribute("totalPages", pageResult.getTotalPages());
        model.addAttribute("pageSize", size);
        model.addAttribute("keyword", keyword);
        model.addAttribute("searchType", searchType);

        return "report/request-list";
    }

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


//=================================================조회 메소드============================================================
//=================================================수정 메소드============================================================

    @GetMapping("/edit") // 수정 페이지 이동
    public String updateReport(@RequestParam("reportId") int reportId, Model model) {

        List<Employee> employees = employeeDAO.getAllEmployees();
        Report report = reportService.getReportById(reportId);

        List<Integer> fileIds = reportFileService.getFileIdsByReportId(reportId);
        // 보고서에 맞는 파일이 있다면 실행
        if (!fileIds.isEmpty()) {
            List<File> files = fileService.getFileListById(fileIds);
            model.addAttribute("files", files);
        }

        model.addAttribute("employees", employees); // employees 목록 추가
        model.addAttribute("report", report);
        return "report/edit";
    }

    // 보고서 수정
    @Transactional
    @PostMapping(value = "/edit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE) // 보고서 수정
    public ResponseEntity<String> updateReport(@RequestPart(value="report") Report report,
                               @RequestPart(value = "reportFiles", required = false) List<MultipartFile> toUploadFileList,
                               @RequestParam(value = "registeredFileIdList", required = false) List<Integer> userSelectedFileIdList) {
        // 요청 수정 권한이 있는지 확인
        UserSessionInfo userSessionInfo = new UserSessionInfo();

        // 현재 로그인한 사용자와 requester_id 비교
        if (report != null && report.getWriterId().equals(userSessionInfo.getCurrentEmployeeId())) {
            reportService.updateReport(report, toUploadFileList, userSelectedFileIdList);
        } else {
            throw new SecurityException("권한이 없습니다.");
        }
        return ResponseEntity.ok("보고서 수정이 완료되었습니다.");
    }

//=================================================수정 메소드============================================================
//=================================================삭제 메소드============================================================

    // 보고서 삭제
    @Transactional
    @DeleteMapping("/delete/{reportId}")
    public String deleteReport(@RequestParam("reportId") int reportId) {
        // 보고서 삭제 권한이 있는지 확인
        // 현재 로그인한 계정의 employeeId를 currentId로 설정
        UserSessionInfo userSessionInfo = new UserSessionInfo();
        String currentId = userSessionInfo.getCurrentEmployeeId();

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
//=====================================================삭제 메소드========================================================

}
