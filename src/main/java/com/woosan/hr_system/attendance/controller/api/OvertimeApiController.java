package com.woosan.hr_system.attendance.controller.api;

import com.woosan.hr_system.attendance.service.OvertimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/overtime")
public class OvertimeApiController {
    @Autowired
    private OvertimeService overtimeService;
}
