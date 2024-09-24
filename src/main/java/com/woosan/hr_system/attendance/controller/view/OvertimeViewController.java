package com.woosan.hr_system.attendance.controller.view;

import com.woosan.hr_system.attendance.service.OvertimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/overtime")
public class OvertimeViewController {
    @Autowired
    private OvertimeService overtimeService;
}
