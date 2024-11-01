package com.woosan.hr_system.attendance.controller.api;

import com.woosan.hr_system.attendance.model.Overtime;
import com.woosan.hr_system.attendance.service.OvertimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/overtime")
public class OvertimeApiController {
    @Autowired
    private OvertimeService overtimeService;

    @PutMapping // 초과근무 수정
    public ResponseEntity<String> editOvertime(@ModelAttribute Overtime overtime) {
        return ResponseEntity.ok(overtimeService.editOvertime(overtime));
    }

    @DeleteMapping("/{id}") // 초과근무 삭제
    public ResponseEntity<String> deleteOvertime(@PathVariable("id") int id) {
        return ResponseEntity.ok(overtimeService.deleteOvertime(id));
    }
}
