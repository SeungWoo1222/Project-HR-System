package com.woosan.hr_system.employee.controller;

import com.woosan.hr_system.auth.aspect.RequireHRPermission;
import com.woosan.hr_system.employee.dao.EmployeeDAO;
import com.woosan.hr_system.employee.model.Employee;
import com.woosan.hr_system.employee.model.Resignation;
import com.woosan.hr_system.employee.service.EmployeeService;
import com.woosan.hr_system.upload.FileService;
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
    private FileService fileService;
    @Autowired
    private EmployeeDAO employeeDAO;

    // 사원 신규 등록
    @RequireHRPermission
    @PostMapping(value = "/registration", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> registerEmployee(@RequestPart("employee") Employee employee,
                                                   @RequestPart("picture") MultipartFile picture) {
        // 파일 체크 후 DB에 저장할 파일명 반환
        employee.setPicture(fileService.checkAndUploadFile(picture));

        // 사원 등록
        employeeService.insertEmployee(employee);
        return ResponseEntity.ok( "'" + employee.getName() + "' 사원이 신규 등록되었습니다.");
    }

    // 사원 정보 수정
    @PutMapping("/update")
    public ResponseEntity<String> updateEmployee(@RequestPart("employee") Employee employee,
                                                 @RequestPart(value = "picture", required = false) MultipartFile picture) {
        // 파일 체크 후 DB에 저장할 파일명 반환
        if (picture != null) {
            employee.setPicture(fileService.checkAndUploadFile(picture));
        }

        // 사원 정보 수정
        employeeService.updateEmployee(employee);
        return ResponseEntity.ok("'" + employee.getName() + "' 사원의 정보가 수정되었습니다.");
    }

    // 계정 잠금 설정
    @RequireHRPermission
    @PatchMapping("/set/accountLock/{employeeId}")
    public ResponseEntity<String> setAccountLock(@PathVariable("employeeId") String employeeId) {
        String message = employeeService.setAccountLock(employeeId);
        return ResponseEntity.ok(message);
    }

    // 사원 퇴사 처리
    @RequireHRPermission
    @PostMapping(value = "/resign/{employeeId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> resignEmployee(@PathVariable("employeeId") String employeeId,
                                                 @RequestPart("resignation") Resignation resignation,
                                                 @RequestPart(value = "resignationDocuments", required = false) MultipartFile[] resignationDocuments) {
        // 파일들 체크 후 DB에 저장할 파일명 반환
        validateFilesAndGetFileName(resignation, resignationDocuments);

        // 사원 퇴사 처리
        employeeService.resignEmployee(employeeId, resignation);
        return ResponseEntity.ok("'" + employeeDAO.getEmployeeById(employeeId).getName() + "' 사원이 퇴사 처리되었습니다.");
    }

    // 사원 퇴사 정보 수정
    @RequireHRPermission
    @PutMapping(value = "/update/resignation/{employeeId}", consumes = "multipart/form-data")
    public ResponseEntity<String> updateResignationInfo(@PathVariable("employeeId") String employeeId,
                                                        @RequestPart("resignation") Resignation resignation,
                                                        @RequestPart(value = "resignationDocuments", required = false) MultipartFile[] resignationDocuments) {
        // 파일들 체크 후 DB에 저장할 파일명 반환
        validateFilesAndGetFileName(resignation, resignationDocuments);

        // 퇴사 정보 수정
        employeeService.updateResignationInfo(employeeId, resignation);
        return ResponseEntity.ok("'" + employeeDAO.getEmployeeById(employeeId).getName() + "' 사원의 퇴사 정보가 수정되었습니다.");
    }

    // 파일 체크들 후 DB에 저장할 파일명 반환 - 퇴사 문서
    private void validateFilesAndGetFileName(Resignation resignation, MultipartFile[] resignationDocuments) {
        if (resignationDocuments != null) {
            employeeService.updateResignationDocuments(resignation, fileService.checkAndUploadFiles(resignationDocuments));
        }
    }
}
