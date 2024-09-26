package com.woosan.hr_system.attendance.controller.api;

import com.woosan.hr_system.attendance.model.Overtime;
import com.woosan.hr_system.attendance.service.OvertimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/overtime")
public class OvertimeApiController {
    @Autowired
    private OvertimeService overtimeService;

    @PutMapping // 근태 수정
    public ResponseEntity<String> editOvertime(@ModelAttribute Overtime overtime) {
        return ResponseEntity.ok(overtimeService.editOvertime(overtime));
    }
}
