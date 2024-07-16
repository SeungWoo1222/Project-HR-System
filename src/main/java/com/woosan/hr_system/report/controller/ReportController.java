package com.woosan.hr_system.report.controller;

import com.woosan.hr_system.report.model.Report;
import com.woosan.hr_system.report.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
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

    @GetMapping("/edit")
    public String editReport(@RequestParam("reportId") Long reportId, Model model) {
        Report report = reportService.getReportById(reportId);
        model.addAttribute("report", report);
        return "report/edit";
    }

    @PostMapping("/update")
    public String updateReport(@RequestParam("reportId") Long reportId,
                               @RequestParam("title") String title,
                               @RequestParam("content") String content,
                               @RequestParam("completeDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate completeDate) {
        reportService.updateReport(reportId, title, content, completeDate);
        return "redirect:/report/" + reportId;
    }

    @GetMapping("/delete/{id}") // 보고서 삭제
    public String deleteReport(@PathVariable("id") Long id) {
        reportService.deleteReport(id);
        return "redirect:/reports";
    }
}
