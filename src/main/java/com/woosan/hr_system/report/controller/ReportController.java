package com.woosan.hr_system.report.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    private EmployeeDAO employeeDAO;
    @Autowired
    private ObjectMapper objectMapper; // 통계 반환 후 view에 보내면서 JSON로 반환함
    @Autowired
    private ReportFileService reportFileService;

    @GetMapping("/main") // main 페이지 이동
    public String getMainPage(HttpSession session,
                              Model model) throws JsonProcessingException {

        // 로그인한 계정 기준 employee_id를 writerId(작성자)로 설정
        UserSessionInfo userSessionInfo = new UserSessionInfo();
        String writerId = userSessionInfo.getCurrentEmployeeId();

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

    //=====================================================생성 메소드======================================================
    @GetMapping("/showCreatePage") // 보고서 생성 페이지 이동
    public String showCreatePage(Model model) {
        List<Employee> employees = employeeDAO.getAllEmployees();
        model.addAttribute("employees", employees); // employees 목록 추가
        model.addAttribute("report", new Report());
        return "report/write";
    }

    // 보고서 생성
    @PostMapping(value = "/write", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> createReport(@ModelAttribute Report report,
                               @RequestParam(value="reportFiles", required=false) List<MultipartFile> reportDocuments) {

        UserSessionInfo userSessionInfo = new UserSessionInfo();
        String writerId = userSessionInfo.getCurrentEmployeeId();
        LocalDateTime currentTime = userSessionInfo.getNow();
        report.setWriterId(writerId);
        report.setCreatedDate(currentTime);

        if (reportDocuments == null || reportDocuments.isEmpty()) {
            reportService.createReport(report);
        } else if (reportDocuments.size() > 1) {
            reportService.createReportWithFile(report, reportDocuments);
        }
        return ResponseEntity.ok("보고서 생성이 완료되었습니다.");
    }

    @GetMapping("/writeFromRequest") // 요청 들어온 보고서 생성 페이지 이동
    public String showCreateFromRequestPage(@RequestParam("requestId") int requestId,
                                            Model model) {

        Request request = requestService.getRequestById(requestId);
        model.addAttribute("request", request);
        model.addAttribute("report", new Report());
        return "/report/write-from-request";
    }

    @PostMapping("/writeFromRequest") // 요청 들어온 보고서 생성
    public ResponseEntity<String> CreateReportFromRequest(
                                          @ModelAttribute Report report,
                                          @RequestParam(value="reportFiles", required=false) List<MultipartFile> reportDocuments,
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
        } else if (reportDocuments != null || !reportDocuments.isEmpty()) {
            int reportId = reportService.createReportFromRequestWithFile(report, reportDocuments);
            requestService.updateReportId(requestId, reportId);
        }
        return ResponseEntity.ok("보고서 생성이 완료되었습니다.");
    }

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

    @GetMapping("/downloadFile")
    public ResponseEntity<ByteArrayResource> downloadFile(
            @RequestParam("fileId") int fileId,
            @RequestParam("originalFileName") String originalFileName) {

        // S3에서 파일 다운로드
        byte[] fileData = fileService.downloadFile(fileId);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + originalFileName + "\"")
                .body(new ByteArrayResource(fileData));
    }


    @GetMapping("/list") // 보고서 리스트 페이지
    public String showReportList(HttpSession session,
                                 @RequestParam(name = "page", defaultValue = "1") int page,
                                 @RequestParam(name = "size", defaultValue = "10") int size,
                                 @RequestParam(name = "keyword", defaultValue = "") String keyword,
                                 @RequestParam(name = "searchType", defaultValue = "1") int searchType,
                                 Model model) {
        // 로그인한 계정 기준 employee_id를 writerId(작성자)로 설정
        UserSessionInfo userSessionInfo = new UserSessionInfo();
        String writerId = userSessionInfo.getCurrentEmployeeId();
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
        UserSessionInfo userSessionInfo = new UserSessionInfo();
        String writerId = userSessionInfo.getCurrentEmployeeId();
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

    @Transactional
    @PostMapping("/edit") // 보고서 수정
    public String updateReport(@ModelAttribute Report report,
                               @RequestParam(value = "reportFileList", required = false) List<MultipartFile> toUploadFileList,
                               @RequestParam(value = "registeredFileIdList", required = false) List<String> registeredFileStringIdList) {

        // List<String>을 List<Integer>로 변환
        List<Integer> registeredFileIdList = new ArrayList<>();
        if (registeredFileStringIdList != null) {
            // JSON으로 변환된 문자열이 들어온 경우 이를 파싱
            for (String fileIdString : registeredFileStringIdList) {
                String parsedString = fileIdString.replaceAll("\\[", "").replaceAll("\\]", "")
                        .replaceAll("\"", "").trim();
                String[] fileIdArray = parsedString.split(",");
                for (String fileId : fileIdArray) {
                    registeredFileIdList.add(Integer.parseInt(fileId.trim()));
                }
            }
        }

        // 업로드된 파일이 없다면 기존의 파일 삭제
        if (toUploadFileList == null || toUploadFileList.isEmpty()) {
            for (int fileId : registeredFileIdList) {
                reportFileService.deleteReportFile(report.getReportId(), fileId);
            }
        }

        // 요청 수정 권한이 있는지 확인
        UserSessionInfo userSessionInfo = new UserSessionInfo();

        // 현재 로그인한 사용자와 requester_id 비교
        if (report != null && report.getWriterId().equals(userSessionInfo.getCurrentEmployeeId())) {
            reportService.updateReport(report, toUploadFileList, registeredFileIdList);
        } else {
            throw new SecurityException("권한이 없습니다.");
        }
        return "report/report-list";
    }

//=================================================수정 메소드============================================================
//=================================================삭제 메소드============================================================

    // 보고서 삭제
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
