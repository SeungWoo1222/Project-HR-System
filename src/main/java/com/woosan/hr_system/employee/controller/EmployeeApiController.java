package com.woosan.hr_system.employee.controller;

import com.woosan.hr_system.auth.aspect.RequireHRPermission;
import com.woosan.hr_system.employee.dao.EmployeeDAO;
import com.woosan.hr_system.employee.model.Employee;
import com.woosan.hr_system.employee.model.Resignation;
import com.woosan.hr_system.employee.service.EmployeeService;
import com.woosan.hr_system.upload.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
        ResponseEntity<String> validationResponse = validateFileAndGetFileName(employee, picture);
        if (validationResponse != null) {
            return validationResponse;
        }

        // 사원 등록
        String message = employeeService.insertEmployee(employee);
        if (message.equals("employeeEmpty")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("입력 정보에서 오류가 발생하였습니다.");
        }
        return ResponseEntity.ok( "'" + employee.getName() + "' 사원이 신규 사원으로 등록되었습니다.");
    }

    // 사원 정보 수정
    @PutMapping("/update")
    public ResponseEntity<String> updateEmployee(@RequestPart("employee") Employee employee,
                                                 @RequestPart(value = "picture", required = false) MultipartFile picture) {
        // 파일 체크 후 DB에 저장할 파일명 반환
        if (picture != null) {
            ResponseEntity<String> validationResponse = validateFileAndGetFileName(employee, picture);
            if (validationResponse != null) {
                return validationResponse;
            }
        }

        // 사원 정보 수정
        String result = employeeService.updateEmployee(employee);
        return switch (result) {
            case "success" -> ResponseEntity.ok("'" + employee.getName() + "' 사원의 정보가 수정되었습니다.");
            case "no_changes" -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("사원 정보의 변경된 사항이 없습니다.");
            case "error" -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body("사원의 정보에서 오류가 발생하였습니다.\n입력된 정보가 올바른지 확인하고 다시 시도해주세요.\n문제가 지속적으로 발생하면 관리자에게 문의해주세요.");
            case "fail" -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("시스템 내부 오류로 인해 사원 정보 수정에 실패하였습니다.\n 잠시 후 다시 시도하거나 시스템 관리자에게 문의하세요.");
            default -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("사원 정보 수정 중 오류가 발생하였습니다.");
        };
    }

    // 파일 체크 후 DB에 저장할 파일명 반환하는 메소드 - 사원 사진
    private ResponseEntity<String> validateFileAndGetFileName(Employee employee, MultipartFile picture) {
        String pictureName;
        try {
            pictureName = fileService.checkFile(picture);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        employee.setPicture(pictureName);
        return null;
    }

    // 사원 퇴사 처리
    @RequireHRPermission
    @PostMapping(value = "/resign/{employeeId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> resignEmployee(@PathVariable("employeeId") String employeeId,
                                                 @RequestPart("resignation") Resignation resignation,
                                                 @RequestPart(value = "resignationDocuments", required = false) MultipartFile[] resignationDocuments) {
        // 파일들 체크 후 DB에 저장할 파일명 반환
        ResponseEntity<String> validationResponse = validateFilesAndGetFileName(resignation, resignationDocuments);
        if (validationResponse != null) return validationResponse;

        // 사원 퇴사 처리
        String message = employeeService.resignEmployee(employeeId, resignation);
        if (message.equals("null")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("'" + employeeDAO.getEmployeeById(employeeId).getName() + "' 사원을 찾을 수 없습니다.");
        }
        return ResponseEntity.ok("'" + employeeDAO.getEmployeeById(employeeId).getName() + "' 사원이 퇴사 처리되었습니다.");
    }

    // 사원 퇴사 정보 수정
    @RequireHRPermission
    @PutMapping(value = "/update/resignation/{employeeId}", consumes = "multipart/form-data")
    public ResponseEntity<String> updateResignationInfo(@PathVariable("employeeId") String employeeId,
                                                        @RequestPart("resignation") Resignation resignation,
                                                        @RequestPart(value = "resignationDocuments", required = false) MultipartFile[] resignationDocuments) {
        // 파일들 체크 후 DB에 저장할 파일명 반환
        ResponseEntity<String> validationResponse = validateFilesAndGetFileName(resignation, resignationDocuments);
        if (validationResponse != null) return validationResponse;

        // 퇴사 정보 수정
        try {
            employeeService.updateResignationInfo(employeeId, resignation);
            return ResponseEntity.ok("'" + employeeDAO.getEmployeeById(employeeId).getName() + "' 사원의 퇴사 정보가 수정되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("퇴사 정보 수정 중 오류가 발생하였습니다.");
        }
    }

    // 파일 체크들 후 DB에 저장할 파일명 반환 - 퇴사 문서
    private ResponseEntity<String> validateFilesAndGetFileName(Resignation resignation, MultipartFile[] resignationDocuments) {
        if (resignationDocuments != null) {
            try {
                String resignationDocumentsName = fileService.checkFiles(resignationDocuments);
                employeeService.updateResignationDocuments(resignation, resignationDocumentsName);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
            }
        }
        return null;
    }

    // 계정 잠금 설정
    @RequireHRPermission
    @PatchMapping("/set/accountLock/{employeeId}")
    public ResponseEntity<String> setAccountLock(@PathVariable("employeeId") String employeeId) {
        try {
            String message = employeeService.setAccountLock(employeeId);
            return ResponseEntity.ok(message);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
