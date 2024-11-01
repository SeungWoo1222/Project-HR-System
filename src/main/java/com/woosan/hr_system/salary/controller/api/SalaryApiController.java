package com.woosan.hr_system.salary.controller.api;

import com.woosan.hr_system.aspect.RequireHRPermission;
import com.woosan.hr_system.salary.model.Salary;
import com.woosan.hr_system.salary.service.SalaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/salary")
public class SalaryApiController {
    @Autowired
    private SalaryService salaryService;

    // 급여 정보 등록
    @RequireHRPermission
    @PostMapping("/register")
    ResponseEntity<String> registerSalaryInfo(@ModelAttribute Salary salary) {
        return ResponseEntity.ok(salaryService.addSalary(salary));
    }

    // 급여 정보 수정
    @RequireHRPermission
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

    // 계좌 정보 수정
    @PatchMapping("/{salaryId}")
    ResponseEntity<String> updateAccountInfo(@PathVariable("salaryId") int salaryId,
                                             @RequestParam("bank") String bank,
                                             @RequestParam("accountNumber") String accountNumber) {
        return ResponseEntity.ok(salaryService.updateAccountInfo(salaryId, bank, accountNumber));
    }

    // 급여 정보 등록 전 사용 중인 급여 확인
    @GetMapping("/check/{employeeId}")
    public ResponseEntity<Map<String, Object>> checkSalaryInfo(@PathVariable("employeeId") String employeeId) {
        Salary salaryInfo = salaryService.hasSalaryInfo(employeeId);

        Map<String, Object> map = new HashMap<>();
        if (salaryInfo != null) {
            map.put("message", "사용 중인 급여 정보가 존재합니다.");
            map.put("salaryInfo", salaryInfo);
            return ResponseEntity.ok(map);
        } else {
            map.put("message", "급여 정보가 존재하지 않습니다.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(map);
        }
    }

    // 급여 정보 사용 중지
    @PatchMapping("/deactivate/{salaryId}")
    public ResponseEntity<String> deactivateSalary(@PathVariable("salaryId") int salaryId) {
        return ResponseEntity.ok(salaryService.deactivateSalary(salaryId));
    }
}
