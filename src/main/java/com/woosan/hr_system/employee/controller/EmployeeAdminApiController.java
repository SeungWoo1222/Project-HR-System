package com.woosan.hr_system.employee.controller;

import com.woosan.hr_system.employee.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/admin/employee")
public class EmployeeAdminApiController {

    @Autowired
    private EmployeeService employeeService;

    // 재직 상태 수정하는 메소드
    @PatchMapping("/update/status/{employeeId}")
    public ResponseEntity<String> updateEmployeeStatus(@PathVariable("employeeId") String employeeId,
                                                       @RequestParam("status") String status) {
        try {
            String message = employeeService.updateStatus(employeeId, status);
            return ResponseEntity.ok(message);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // 사원 승진 처리하는 메소드
    @PatchMapping("/promote/{employeeId}")
    public ResponseEntity<String> promoteEmployee(@PathVariable("employeeId") String employeeId) {
        try {
            String message = employeeService.promoteEmployee(employeeId);
            return ResponseEntity.ok(message);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
