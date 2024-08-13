package com.woosan.hr_system.employee.controller;

import com.woosan.hr_system.auth.aspect.RequireHRPermission;
import com.woosan.hr_system.auth.service.AuthService;
import com.woosan.hr_system.employee.model.Employee;
import com.woosan.hr_system.employee.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    @PostMapping(value = "/registration", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> registerEmployee(@RequestPart("employee") Employee employee,
                                                   @RequestPart("picture") MultipartFile picture) {
        // 업로드 후 사진 파일ID 할당
        employeeService.assignPictureFromUpload(employee, picture);

        // 사원 등록
        return ResponseEntity.ok(employeeService.insertEmployee(employee));
    }

    // 사원 정보 수정
    @PutMapping("/update")
    public ResponseEntity<String> updateEmployee(@RequestPart("employee") Employee employee,
                                                 @RequestPart(value = "picture", required = false) MultipartFile picture) {
        // 파일 체크 후 업로드 후 사진 파일ID 할당
        if (picture != null) {
            employeeService.assignPictureFromUpload(employee, picture);
        }

        // 사원 정보 수정
        return ResponseEntity.ok(employeeService.updateEmployee(employee));
    }

    // 계정 잠금 설정
    @RequireHRPermission
    @PatchMapping("/set/accountLock/{employeeId}")
    public ResponseEntity<String> setAccountLock(@PathVariable("employeeId") String employeeId) {
        return ResponseEntity.ok(authService.setAccountLock(employeeId));
    }
}
