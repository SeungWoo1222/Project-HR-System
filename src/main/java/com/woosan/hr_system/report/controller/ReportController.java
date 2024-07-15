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

    @GetMapping("/{id}") // 특정 보고서 조회
    public String getReportById(@PathVariable("id") Long id, Model model) {
        Report report = reportService.getReportById(id);
        model.addAttribute("report", report);
        return "report/detail";
    }

//    @GetMapping("/write") // 보고서 작성
//    public String showCreateForm(Model model) {
//        model.addAttribute("report", new Report());
//        return "report/write";
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
            // 보고서 저장
            Report report = new Report();
            report.setTitle(title);
            report.setContent(content);
            report.setApproverId(approverId);
            report.setCreatedDate(new Timestamp(System.currentTimeMillis())); //현재 시간으로 설정
            report.setModifiedDate(new Timestamp(System.currentTimeMillis())); //현재 시간으로 설정


            // completeDate를 Date로 변환
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate localDate = LocalDate.parse(completeDate, formatter);
                report.setCompleteDate(Date.valueOf(localDate));
            } catch (Exception e) {
                model.addAttribute("message", "날짜 형식이 잘못되었습니다.");
                return "report/write";
            }

            reportService.insertReport(report);

            // 파일이 있는 경우 파일 첨부 메소드 실행
            if (!file.isEmpty()) {
                reportService.uploadFiles(report.getReportId(), new MultipartFile[]{file});
            }

            model.addAttribute("message", "보고서 작성 완료");
            return "redirect:/report/report-home";

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
