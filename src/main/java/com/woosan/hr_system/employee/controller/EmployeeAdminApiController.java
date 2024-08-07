package com.woosan.hr_system.employee.controller;

import com.woosan.hr_system.auth.aspect.RequireHRPermission;
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
    @RequireHRPermission
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
    @RequireHRPermission
    @PatchMapping("/promote/{employeeId}")
    public ResponseEntity<String> promoteEmployee(@PathVariable("employeeId") String employeeId) {
        try {
            String message = employeeService.promoteEmployee(employeeId);
            return ResponseEntity.ok(message);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // 사원 영구 삭제
    @RequireHRPermission
    @DeleteMapping("/delete/{employeeId}")
    public ResponseEntity<String> deleteEmployee(@PathVariable("employeeId") String employeeId) {
        String message = employeeService.deleteEmployee(employeeId);
        return switch (message) {
            case "success" -> ResponseEntity.ok("사원이 삭제되었습니다.");
            case "null" -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 사원을 찾을 수 없습니다.");
            case "no_resignation" -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body("해당 사원의 퇴사 정보가 없습니다.");
            case "not_expired" -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body("퇴사 후 1년이 지나지 않았습니다.");
            default -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("삭제하는 중 오류가 발생했습니다.");
        };
    }
}
