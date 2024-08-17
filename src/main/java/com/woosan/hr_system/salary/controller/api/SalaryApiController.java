package com.woosan.hr_system.salary.controller.api;

import com.woosan.hr_system.auth.aspect.RequireHRPermission;
import com.woosan.hr_system.salary.model.Salary;
import com.woosan.hr_system.salary.service.SalaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/salary")
public class SalaryApiController {
    @Autowired
    private SalaryService salaryService;

    // 급여 정보 등록
    @RequireHRPermission
    @PostMapping("/register")
    ResponseEntity<String> registerSalaryInfo(@RequestBody Salary salary, @RequestParam String employeeId) {
        return ResponseEntity.ok(salaryService.addSalary(salary, employeeId));
    }

    // 급여 정보 수정
    @PutMapping("/{employeeId}")
    ResponseEntity<String> updateSalaryInfo(@PathVariable String employeeId, @RequestBody Salary salary) {
        return ResponseEntity.ok(salaryService.updateSalary(salary, employeeId));
    }

    // 급여 정보 삭제
    @RequireHRPermission
    @DeleteMapping("/{salaryId}")
    ResponseEntity<String> deleteSalaryInfo(@PathVariable int salaryId) {
        return ResponseEntity.ok(salaryService.removeSalary(salaryId));
    }
}
