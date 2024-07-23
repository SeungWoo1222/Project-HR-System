package com.woosan.hr_system.report.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.woosan.hr_system.report.model.Report;
import com.woosan.hr_system.report.model.ReportStat;
import com.woosan.hr_system.report.model.Request;
import com.woosan.hr_system.report.service.RequestService;
import com.woosan.hr_system.report.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
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
    private ObjectMapper objectMapper; // JSON 변환을 위한 ObjectMapper

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
        return "report/statistic";  // write.html로 연결
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

    @GetMapping("/{reportId}") // 특정 보고서 조회
    public String viewReport(@PathVariable("reportId") Long reportId, Model model) {
        Report report = reportService.getReportById(reportId);
        model.addAttribute("report", report);

        if (report.getFileId() != null) {
            FileMetadata reportFile = reportService.getReportFileById(report.getFileId());
            model.addAttribute("reportFile", reportFile);
        }
        return "report/view";
    }

    @GetMapping("/write") // 보고서 작성 페이지 이동
    public String showCreateForm(Model model) {
        model.addAttribute("employees", requestService.getEmployees()); // employees 목록 추가
        return "report/write";  // write.html로 연결
    }

    @PostMapping("/write") // 보고서 생성
    public String createReport(@RequestParam("title") String title,
                               @RequestParam("content") String content,
                               @RequestParam("approverId") String approverId,
                               @RequestParam("completeDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate completeDate,
                               @RequestParam("file") MultipartFile file,
                               Model model) {
        try {
            // report 객체 설정
            Report report = new Report();
            report.setTitle(title);
            report.setContent(content);
            report.setApproverId(approverId);
            report.setCompleteDate(completeDate);


            reportService.createReport(report, file);

            model.addAttribute("message", "보고서 작성 완료");
            return "redirect:/report/main";
        } catch (DateTimeParseException e) {
            model.addAttribute("message", "날짜 형식이 잘못되었습니다.");
            return "report/write";
        } catch (IOException e) {
            model.addAttribute("message", "파일 업로드 실패");
            e.printStackTrace();
            return "report/write";
        }
    }

    @GetMapping("/edit") // 수정 페이지 이동
    public String updateReport(@RequestParam("reportId") Long reportId, Model model) {
        Report report = reportService.getReportById(reportId);
        model.addAttribute("employees", requestService.getEmployees()); // employees 목록 추가
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
            return "redirect:/report/main";
        } catch (Exception e) {
            return "error"; // 에러 메시지 표시
        }
    }

    @DeleteMapping("/delete/{reportId}") // 보고서 삭제
    public String deleteReport(@PathVariable("reportId") Long id, RedirectAttributes redirectAttributes) {
        reportService.deleteReport(id);
        return "redirect:/report/main";
    }
}
