package com.woosan.hr_system.report.controller;

import com.woosan.hr_system.report.model.Request;
import com.woosan.hr_system.report.service.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/request")
public class RequestController {
    @Autowired
    private RequestService requestService;

    @GetMapping("/{requestId}") // 특정 요청 조회
    public String viewRequest(@PathVariable("requestId") Long requestId, Model model) {
        Request request = requestService.getRequestById(requestId);
        model.addAttribute("request", request);
        return "request_view";
    }

}