package com.woosan.hr_system.report.controller;

import com.woosan.hr_system.report.model.Request;
import com.woosan.hr_system.report.service.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@Controller
@RequestMapping("/request")
public class RequestController {

    @Autowired
    private RequestService requestService;

    @GetMapping("/write") // 요청 생성 페이지 이동
    public String showRequestForm(Model model) {
        model.addAttribute("request", new Request());
        return "report/request/request-write";
    }

    @PostMapping("/write") // 요청 생성
    public String createRequest(@RequestParam("employeeId") String employeeId,
                                @RequestParam("dueDate") String dueDate,
                                @RequestParam("requestNote") String requestNote,
                                Model model) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate dueDateSql = LocalDate.parse(dueDate, formatter);
            LocalDateTime requestDate = null; //생성시간 표시용

            requestService.createRequest(employeeId, dueDateSql, requestNote, requestDate);

            model.addAttribute("message", "보고서 작성 완료");
            return "redirect:/report/report-home";
        } catch (DateTimeParseException e) {
            model.addAttribute("message", "날짜 형식이 잘못되었습니다.");
            return "report/request/request-write";
        }
    }

    @GetMapping("/get-all-report-requests") // 모든 보고서 요청 목록 조회
    @ResponseBody
    public List<Request> getAllReportRequests() {
        return requestService.getAllReportRequests();
    }

    @GetMapping("/{requestId}") // 특정 요청 조회
    public String viewRequest(@PathVariable("requestId") Long requestId, Model model) {
        Request request = requestService.getRequestById(requestId);
        model.addAttribute("request", request);
        return "report/request/request-view";
    }

    @GetMapping("/edit") // 요청 수정 페이지 이동
    public String editRequest(@RequestParam(name = "requestId") Long requestId, Model model) {
        Request request = requestService.getRequestById(requestId);
        model.addAttribute("request", request);
        return "report/request/request-edit";
    }

    @PostMapping("/update") // 요청 수정
    public String updateRequest(@RequestParam("requestId") Long requestId,
                                @RequestParam("employeeId") String employeeId,
                                @RequestParam("requestNote") String requestNote,
                                @RequestParam("dueDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dueDate) {
        requestService.updateRequest(requestId, employeeId, requestNote, dueDate);
        return "redirect:/request/" + requestId;
    }

    @DeleteMapping("/delete/{requestId}") // 보고서 삭제
    public String deleteRequest(@PathVariable("requestId") Long id, RedirectAttributes redirectAttributes) {
        requestService.deleteRequest(id);
        return "redirect:/report/report-home";
    }







//    @PostMapping("/create") //  요청 생성
//    public String createReportRequest(@RequestBody ReportRequest request) {
//        reportRequestService.createReportRequest(request);
//        return "Report request created successfully";
//    }
//
//    @GetMapping("/{id}") // 보고서 기반 작성 요청 조회
//    public ReportRequest getReportRequestById(@PathVariable int id) {
//        return reportRequestService.getReportRequestById(id);
//    }
//
//    @PutMapping("/update") // 작성 요청 수정
//    public String updateReportRequest(@RequestBody ReportRequest request) {
//        reportRequestService.updateReportRequest(request);
//        return "Report request updated successfully";
//    }
//
//    @DeleteMapping("/{id}") // 특정 작성 요청 삭제
//    public String deleteReportRequest(@PathVariable int id) {
//        reportRequestService.deleteReportRequest(id);
//        return "Report request deleted successfully";
//    }
//
//    @GetMapping("/employee/{employeeId}") // 사원 기반 보고서 조회
//    public List<ReportRequest> getReportRequestsByEmployeeId(@PathVariable String employeeId) {
//        return reportRequestService.getReportRequestsByEmployeeId(employeeId);
//    }
}
