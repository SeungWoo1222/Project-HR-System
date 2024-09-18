package com.woosan.hr_system.attendance.controller;

import com.woosan.hr_system.attendance.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/attendance")
public class AttendanceViewController {
    @Autowired
    private AttendanceService attendanceService;

}
