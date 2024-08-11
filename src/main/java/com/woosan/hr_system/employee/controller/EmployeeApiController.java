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
    @PostMapping(value = "/registration", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> registerEmployee(@RequestPart("employee") Employee employee,
                                                   @RequestPart("picture") MultipartFile picture) {
        // 파일 체크 후 DB에 저장할 파일명 반환
        employeeService.assignPictureFromUpload(employee, picture);

        // 사원 등록
        String name = employeeService.insertEmployee(employee);
        return ResponseEntity.ok( "'" + name + "' 사원이 신규 등록되었습니다.");
    }

    // 사원 정보 수정
    @PutMapping("/update")
    public ResponseEntity<String> updateEmployee(@RequestPart("employee") Employee employee,
                                                 @RequestPart(value = "picture", required = false) MultipartFile picture) {
        // 파일 체크 후 DB에 저장할 파일명 반환
        if (picture != null) {
            employeeService.assignPictureFromUpload(employee, picture);
        }

        // 사원 정보 수정
        String name = employeeService.updateEmployee(employee);
        return ResponseEntity.ok("'" + name + "' 사원의 정보가 수정되었습니다.");
    }

    // 계정 잠금 설정
    @RequireHRPermission
    @PatchMapping("/set/accountLock/{employeeId}")
    public ResponseEntity<String> setAccountLock(@PathVariable("employeeId") String employeeId) {
        String message = authService.setAccountLock(employeeId);
        return ResponseEntity.ok(message);
    }

    // 부서 id를 이용한 해당 부서 사원 조회
    @GetMapping("/department/list/{departmentId}")
    public List<Employee> getEmployeesByDepartment(@PathVariable("departmentId") String departmentId) {
        List<Employee> employeeList = employeeService.getEmployeesByDepartment(departmentId);
        return employeeList;
    }
}
