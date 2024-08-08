package com.woosan.hr_system.employee.controller;

import com.woosan.hr_system.employee.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/admin/employee")
public class EmployeeAdminApiController {

    @Autowired
    private EmployeeService employeeService;

    // 재직 상태 수정하는 메소드
//    @RequireHRPermission
    @PatchMapping("/update/status/{employeeId}")
    public ResponseEntity<String> updateEmployeeStatus(@PathVariable("employeeId") String employeeId,
                                                       @RequestParam("status") String status) {
        return ResponseEntity.ok(employeeService.updateStatus(employeeId, status));
    }

    // 사원 승진 처리하는 메소드
//    @RequireHRPermission
    @PatchMapping("/promote/{employeeId}")
    public ResponseEntity<String> promoteEmployee(@PathVariable("employeeId") String employeeId) {
        return ResponseEntity.ok(employeeService.promoteEmployee(employeeId));
    }

    // 사원 영구 삭제
//    @RequireHRPermission
    @DeleteMapping("/delete/{employeeId}")
    public ResponseEntity<String> deleteEmployee(@PathVariable("employeeId") String employeeId) {
        return ResponseEntity.ok(employeeService.deleteEmployee(employeeId));
    }
}
