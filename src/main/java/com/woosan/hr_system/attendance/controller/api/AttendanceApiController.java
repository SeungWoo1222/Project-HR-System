package com.woosan.hr_system.attendance.controller.api;

import com.woosan.hr_system.attendance.model.Attendance;
import com.woosan.hr_system.attendance.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceApiController {
    @Autowired
    private AttendanceService attendanceService;

    @PostMapping // 출근
    public ResponseEntity<String> checkIn() {
        return ResponseEntity.ok(attendanceService.checkIn());
    }

    @PatchMapping // 퇴근
    public ResponseEntity<String> checkOut() {
        return ResponseEntity.ok(attendanceService.checkOut());
    }

    @PatchMapping("/earlyLeave") // 조퇴
    public ResponseEntity<String> earlyLeave(@RequestBody Map<String, String> requestData) {
        return ResponseEntity.ok(attendanceService.earlyLeave(requestData.get("notes")));
    }

    @PutMapping // 근태 수정
    public ResponseEntity<String> editAttendance(@ModelAttribute Attendance attendance) {
        return ResponseEntity.ok(attendanceService.editAttendance(attendance));
    }

    @GetMapping("/{employeeId}") // 사원 근태 기록 조회
    public List<Attendance> getEmployeeAttendance(@PathVariable("employeeId") String employeeId) {
        return attendanceService.getAttendanceByEmployeeId(employeeId);
    }
}
