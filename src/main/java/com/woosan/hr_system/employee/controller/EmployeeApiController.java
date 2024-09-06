package com.woosan.hr_system.employee.controller;

import com.woosan.hr_system.aspect.RequireHRPermission;
import com.woosan.hr_system.auth.service.AuthService;
import com.woosan.hr_system.employee.model.Employee;
import com.woosan.hr_system.employee.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/employee")
public class EmployeeApiController {
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private AuthService authService;

    // 사원 신규 등록
    @RequireHRPermission
    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> registerEmployee(@RequestPart("employee") Employee employee,
                                                   @RequestPart("picture") MultipartFile picture) {
        // 사원 등록
        return ResponseEntity.ok(employeeService.insertEmployee(employee, picture));
    }

    // 사원 정보 수정
    @PutMapping("/update")
    public ResponseEntity<String> updateEmployee(@RequestPart("employee") Employee employee,
                                                 @RequestPart(value = "picture", required = false) MultipartFile picture) {
        // 사원 정보 수정
        return ResponseEntity.ok(employeeService.updateEmployee(employee, picture));
    }

    // 계정 잠금 설정
    @RequireHRPermission
    @PatchMapping("/{employeeId}/accountLock")
    public ResponseEntity<String> setAccountLock(@PathVariable("employeeId") String employeeId) {
        return ResponseEntity.ok(authService.setAccountLock(employeeId));
    }

    // 부서 id를 이용한 해당 부서 사원 조회
    @GetMapping("/department/list/{departmentId}")
    public List<Employee> getEmployeesByDepartment(@PathVariable("departmentId") String departmentId) {
        return employeeService.getEmployeesByDepartment(departmentId);
    }
}
