package com.woosan.hr_system.attendance.controller;

import com.woosan.hr_system.attendance.service.AttendanceService;
import com.woosan.hr_system.auth.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/attendance")
public class AttendanceViewController {
    @Autowired
    private AttendanceService attendanceService;
    @Autowired
    private AuthService authService;

    @GetMapping// 나의 근태 페이지
    public String viewMyAttendance(Model model) {
        // 로그인 사원 ID 조회
        String employeeId = authService.getAuthenticatedUser().getUsername();
        model.addAttribute("attendanceList", attendanceService.getAttendanceByEmployeeId(employeeId));
        return "attendance/my-log";
    }

    @GetMapping("/{attendanceId}") // 근태 상세 페이지
    public String viewAttendanceDetail(@PathVariable("attendanceId") int attendanceId, Model model) {
        // 근태 정보 상세 조회
        model.addAttribute(attendanceService.getAttendanceById(attendanceId));
        return "attendance/detail";
    }

    @GetMapping("/commute") // 출퇴근 페이지
    public String viewCommuteModal(Model model) {
        // 로그인한 사원의 금일 근태기록 있는지 확인 후 모델에 추가
        model.addAttribute("attendance", attendanceService.hasTodayAttendanceRecord());
        return "attendance/commute";
    }

    @GetMapping("/early-leave") // 조퇴 페이지
    public String viewEarlyLeaveModal(Model model) {
        // 로그인한 사원의 금일 근태기록 있는지 확인 후 모델에 추가
        model.addAttribute("attendance", attendanceService.hasTodayAttendanceRecord());
        return "attendance/early-leave";
    }
}
