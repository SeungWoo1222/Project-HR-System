package com.woosan.hr_system.report.controller;

import com.woosan.hr_system.report.model.Report;
import com.woosan.hr_system.report.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping // 모든 보고서 조회
    public String getAllReports(Model model) {
        List<Report> reports = reportService.getAllReports();
        model.addAttribute("reports", reports);
        return "report/list";
    }

    @GetMapping("/{id}") // 특정 보고서 조회
    public String getReportById(@PathVariable("id") int id, Model model) {
        Report report = reportService.getReportById(id);
        model.addAttribute("report", report);
        return "report/detail";
    }

    @GetMapping("/new") // 보고서 작성 페이지 이동
    public String showCreateForm(Model model) {
        model.addAttribute("report", new Report());
        return "report/form";
    }

    @PostMapping // 보고서 등록
    public String createReport(@ModelAttribute Report report) {
        reportService.insertReport(report);
        return "redirect:/reports";
}

    @GetMapping("/edit/{id}") // 보고서 수정 페이지 이동
    public String showEditForm(@PathVariable("id") int id, Model model) {
        Report report = reportService.getReportById(id);
        model.addAttribute("report", report);
        return "report/form";
    }

    @PostMapping("/update/{id}") // 보고서 수정
    public String updateReport(@PathVariable("id") int id, @ModelAttribute Report report) {
        report.setReportId(id);
        reportService.updateReport(report);
        return "redirect:/reports";
    }

    @GetMapping("/delete/{id}") // 보고서 삭제
    public String deleteReport(@PathVariable("id") int id) {
        reportService.deleteReport(id);
        return "redirect:/reports";
    }
}
