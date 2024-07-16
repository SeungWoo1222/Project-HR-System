package com.woosan.hr_system.report.controller;

import com.woosan.hr_system.report.model.Report;
import com.woosan.hr_system.report.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.woosan.hr_system.report.model.FileMetadata;

import java.io.IOException;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;


@Controller
@RequestMapping("/report")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping("/report-home") // 모든 보고서 조회
    public String getAllReports(Model model) {
        List<Report> reports = reportService.getAllReports();
        model.addAttribute("reports", reports);
        return "report/report-home";
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

//    @GetMapping("/{id}") // 특정 보고서 조회
//    public String getReportById(@PathVariable("id") Long id, Model model) {
//        Report report = reportService.getReportById(id);
//        model.addAttribute("report", report);
//        return "report/detail";
//    }

    @GetMapping("/write") // 보고서 작성페이지 이동
    public String showCreateForm(Model model) {
        model.addAttribute("report", new Report());
        return "report/write";  // write.html로 연결
    }

    @PostMapping("/write") // 보고서 작성
    public String createReport(@RequestParam("title") String title,
                               @RequestParam("content") String content,
                               @RequestParam("approverId") String approverId,
                               @RequestParam("completeDate") String completeDate,
                               @RequestParam("file") MultipartFile file,
                               Model model) {
        try {
            // completeDate를 Date로 변환
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate localDate = LocalDate.parse(completeDate, formatter);
            Date completeDateSql = Date.valueOf(localDate);

            // 보고서 저장을 서비스 레이어에 위임
            reportService.createReport(title, content, approverId, completeDateSql, file);

            model.addAttribute("message", "보고서 작성 완료");
            return "redirect:/report/report-home";
        } catch (DateTimeParseException e) {
            model.addAttribute("message", "날짜 형식이 잘못되었습니다.");
            return "report/write";
        } catch (IOException e) {
            model.addAttribute("message", "파일 업로드 실패");
            e.printStackTrace();
            return "/write";
        }
    }

    @GetMapping("/edit/{id}") // 보고서 수정 페이지 이동
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Report report = reportService.getReportById(id);
        model.addAttribute("report", report);
        return "report/form";
    }

    @PostMapping("/update/{id}") // 보고서 수정
    public String updateReport(@PathVariable("id") Long id, @ModelAttribute Report report) {
        report.setReportId(id);
        reportService.updateReport(report);
        return "redirect:/reports";
    }

    @GetMapping("/delete/{id}") // 보고서 삭제
    public String deleteReport(@PathVariable("id") Long id) {
        reportService.deleteReport(id);
        return "redirect:/reports";
    }

//    @PostMapping
//    public List<FileMetadata> uploadFiles(@PathVariable Long reportId, @RequestParam("files") MultipartFile[] files) {
//        try {
//            return reportService.uploadFiles(reportId, files);
//        } catch (IOException e) {
//            e.printStackTrace();
//            throw new RuntimeException("File upload failed");
//        }
//    }
}
