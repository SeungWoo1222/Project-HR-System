package com.woosan.hr_system.report.controller;

import com.woosan.hr_system.auth.CustomUserDetails;
import com.woosan.hr_system.employee.service.EmployeeService;
import com.woosan.hr_system.report.model.Request;
import com.woosan.hr_system.report.service.RequestService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    public String showWritePage(Model model) {
        model.addAttribute("employees", requestService.getEmployees()); // employees 목록 추가
        return "report/request/write";
    }

    @PostMapping("/write") // 요청 생성
    public String createRequest(@RequestParam("employeeName") String employeeName,
                                @RequestParam("dueDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dueDate,
                                @RequestParam("requestNote") String requestNote,
                                Model model) {
        try {
            // request 객체 설정
            Request request = new Request();

            request.setEmployeeName(employeeName);
            request.setDueDate(dueDate);
            request.setRequestNote(requestNote);

            requestService.createRequest(request);

            model.addAttribute("message", "보고서 작성 완료");
            return "redirect:/report/main";
        } catch (DateTimeParseException e) {
            model.addAttribute("message", "날짜 형식이 잘못되었습니다.");
            return "report/request/write";
        }
    }

    @GetMapping("/{requestId}") // 특정 요청 조회
    public String viewRequest(@PathVariable("requestId") Long requestId, Model model) {
        Request request = requestService.getRequestById(requestId);
        model.addAttribute("request", request);
        return "/report/request/view";
    }

    @GetMapping("/edit") // 요청 수정 페이지 이동
    public String editRequest(@RequestParam(name = "requestId") Long requestId, Model model) {
        Request request = requestService.getRequestById(requestId);
        model.addAttribute("request", request);
        return "report/request/edit";
    }

    @PostMapping("/edit") // 요청 수정
    public String updateRequest(@RequestParam("requestId") Long requestId,
                                @RequestParam("employeeId") String employeeId,
                                @RequestParam("requestNote") String requestNote,
                                @RequestParam("dueDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dueDate) {
        //request 객체 설정
        Request request = new Request();
        request.setRequestId(requestId);
        request.setEmployeeId(employeeId);
        request.setRequestNote(requestNote);
        request.setDueDate(dueDate);

        requestService.updateRequest(request);
        return "redirect:/request/" + requestId;
    }

    @DeleteMapping("/delete/{requestId}") // 보고서 삭제
    public String deleteRequest(@PathVariable("requestId") Long id, RedirectAttributes redirectAttributes) {
        requestService.deleteRequest(id);
        return "redirect:/report/main";
    }
}