package com.woosan.hr_system.employee.controller;

import com.woosan.hr_system.auth.AuthService;
import com.woosan.hr_system.employee.model.Department;
import com.woosan.hr_system.employee.model.Position;
import com.woosan.hr_system.search.PageRequest;
import com.woosan.hr_system.search.PageResult;
import com.woosan.hr_system.employee.model.Employee;
import com.woosan.hr_system.employee.service.EmployeeService;
import com.woosan.hr_system.upload.S3Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/employee")
public class EmployeeController {
    private static final Logger logger = LoggerFactory.getLogger(EmployeeController.class);

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private S3Service s3Service;

    @Autowired
    private AuthService authService;
    // ============================================ 조회 관련 로직 start-point ============================================
    @GetMapping("/list") // 모든 사원 정보 조회
    public String getEmployees(@RequestParam(name = "page", defaultValue = "1") int page,
                               @RequestParam(name = "size", defaultValue = "10") int size,
                               @RequestParam(name = "keyword", defaultValue = "") String keyword,
                               Model model) {

        // 매개변수 값 로그에 출력
        logger.debug("‼️Page: {}, Size: {}, Keyword: {} ‼️", page, size, keyword);

        PageRequest pageRequest = new PageRequest(page - 1, size, keyword); // 페이지 번호 인덱싱을 위해 다시 -1
        PageResult<Employee> pageResult = employeeService.searchEmployees(pageRequest);

        model.addAttribute("employees", pageResult.getData());
        model.addAttribute("currentPage", pageResult.getCurrentPage() + 1); // 뷰에서 가독성을 위해 +1
        model.addAttribute("totalPages", pageResult.getTotalPages());
        model.addAttribute("pageSize", size);
        model.addAttribute("keyword", keyword);
        return "employee/list";
    }

    @GetMapping("/{employeeId}") // 사원 정보 상세 조회
    public String getEmployee(@PathVariable("employeeId") String employeeId, Model model) {
        Employee employee = employeeService.getEmployeeById(employeeId);
        if (employee == null) {
            return "error/404";
        }
        String pictureUrl = s3Service.getUrl(employee.getPicture());
        model.addAttribute("pictureUrl", pictureUrl);
        model.addAttribute("employee", employee);
        return "employee/detail";
    }

    @GetMapping("/myInfo") // 내 정보 조회
    public String viewMyInfo(Model model) {
        String employeeId = authService.getAuthenticatedUser().getUsername();
        Employee employee = employeeService.getEmployeeById(employeeId);
        if (employee == null) {
            return "error/404";
        }
        String pictureUrl = s3Service.getUrl(employee.getPicture());
        model.addAttribute("pictureUrl", pictureUrl);
        model.addAttribute("employee", employee);
        return "employee/myInfo";
    }
    // ============================================= 조회 관련 로직 end-point =============================================

    // ============================================ 등록 관련 로직 start-point ============================================
    @GetMapping("/registration") // 신규 사원 등록 페이지 이동
    public String viewEmployeeForm() {
        return "employee/registration";
    }

    @PostMapping(value = "/registration", consumes = MediaType.MULTIPART_FORM_DATA_VALUE) // 신규 사원 등록
    public ResponseEntity<String> registerEmployee(@RequestParam("name") String name,
                                                   @RequestParam("birth") String birth,
                                                   @RequestParam("residentRegistrationNumber") String residentRegistrationNumber,
                                                   @RequestParam("phone") String phone,
                                                   @RequestParam("email") String email,
                                                   @RequestParam("address") String address,
                                                   @RequestParam("detailAddress") String detailAddress,
                                                   @RequestParam("department") Department department,
                                                   @RequestParam("position") Position position,
                                                   @RequestParam("hireDate") LocalDate hireDate,
                                                   @RequestParam("picture") MultipartFile picture) {
        // 파일 도착 확인
        logger.debug("‼️Received picture - File name: {}, Size: {}, Content Type: {} ‼️", picture.getOriginalFilename(), picture.getSize(), picture.getContentType());

        // 파일 체크 후 DB에 저장할 파일명 반환
        String pictureName;
        String checkMessage = s3Service.checkFile(picture);
        if (checkMessage.equals("empty")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("파일이 비어있습니다.\n파일을 확인 후 재업로드해주세요.");
        } else if (checkMessage.equals("fail")) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("파일 업로드 중 오류가 발생하였습니다.\n파일 확인 후 재업로드 또는 관리자에게 문의해주세요.");
        } else {
            pictureName = checkMessage;
        }

        // Employee 객체 생성 및 설정
        Employee employee = new Employee();
        employee.setName(name);
        employee.setBirth(birth);
        employee.setResidentRegistrationNumber(residentRegistrationNumber);
        employee.setPhone(phone);
        employee.setEmail(email);
        employee.setAddress(address);
        employee.setDetailAddress(detailAddress);
        employee.setDepartment(department);
        employee.setPosition(position);
        employee.setHireDate(hireDate);
        employee.setPicture(pictureName);

        // 사원 등록
        String message = employeeService.insertEmployee(employee);
        if (message.equals("employeeEmpty")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("입력 정보에서 오류가 발생하였습니다.");
        }
        return ResponseEntity.ok( "'" + employee.getName() + "' 사원이 신규 사원으로 등록되었습니다.");
    }
    // ============================================= 등록 관련 로직 end-point =============================================

    // ============================================ 수정 관련 로직 start-point ============================================
    @GetMapping("/edit/{employeeId}") // 사원 정보 수정 페이지 이동
    public String editEmployeeForm(@PathVariable("employeeId") String employeeId, Model model) {
        Employee employee = employeeService.getEmployeeById(employeeId);
        model.addAttribute("employee", employee);
        return "employee/edit";
    }

    @GetMapping("/edit/myInfo/{employeeId}") // 내 정보 수정 페이지 이동
    public String editMyInfoForm(@PathVariable("employeeId") String employeeId, Model model) {
        Employee employee = employeeService.getEmployeeById(employeeId);
        String pictureUrl = s3Service.getUrl(employee.getPicture());
        model.addAttribute("pictureUrl", pictureUrl);
        model.addAttribute("employee", employee);
        return "employee/edit/myInfo";
    }

    @PutMapping("/update") // 사원 정보 수정
    public ResponseEntity<String> updateEmployee(Employee employee) {
        String result = employeeService.updateEmployee(employee);
        return switch (result) {
            case "success" -> ResponseEntity.ok("'" + employee.getEmployeeId() + "' 사원의 정보가 수정되었습니다.");
            case "no_changes" -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("사원 정보의 변경된 사항이 없습니다.");
            case "error" -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body("사원의 정보에서 오류가 발생하였습니다.\n입력된 정보가 올바른지 확인하고 다시 시도해주세요.\n문제가 지속적으로 발생하면 관리자에게 문의해주세요.");
            case "fail" -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("시스템 내부 오류로 인해 사원 정보 수정에 실패하였습니다.\n 잠시 후 다시 시도하거나 시스템 관리자에게 문의하세요.");
            default -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("사원 정보 수정 중 오류가 발생하였습니다.");
        };
    }
    // ============================================= 수정 관련 로직 end-point =============================================

    // ============================================ 퇴사 관련 로직 start-point ============================================
    @GetMapping("/resignation") // 사원 퇴사 관리 페이지 이동
    public String viewResignationManagement(Model model) {
        List<Employee> preResignationEmployees = employeeService.getPreResignationEmployees();
        List<Employee> resignedEmployees = employeeService.getResignedEmployees();
        List<Employee> preDeletionEmployees = employeeService.getPreDeletionEmployees();
        model.addAttribute("preResignationEmployees", preResignationEmployees);
        model.addAttribute("resignedEmployees", resignedEmployees);
        model.addAttribute("preDeletionEmployees", preDeletionEmployees);
        return "/employee/resignation";
    }

    @GetMapping("/resignation-form/{employeeId}") // 사원 퇴사 처리 폼 페이지 이동
    public String viewEmployeeForResignation(@PathVariable("employeeId") String employeeId, Model model) {
        Employee employee = employeeService.getEmployeeById(employeeId);
        if (employee == null) {
            return "error/employee-error";
        }
        String pictureUrl = s3Service.getUrl(employee.getPicture());
        model.addAttribute("pictureUrl", pictureUrl);
        model.addAttribute("employee", employee);
        return "employee/resignation-form";
    }

    @PostMapping(value = "/resign/{employeeId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE) // 사원 퇴사 처리 로직
    public ResponseEntity<String> resignEmployee(@PathVariable("employeeId") String employeeId,
                                    @RequestParam("resignationReason") String resignationReason,
                                    @RequestParam("codeNumber") String codeNumber,
                                    @RequestParam("specificReason") String specificReason,
                                    @RequestParam("resignationDate") LocalDate resignationDate,
                                    @RequestParam("resignationDocuments") MultipartFile[] resignationDocuments) {
        // 파일 도착 확인 로그
        logger.debug("Received resignation documents :");
        for (MultipartFile file : resignationDocuments) {
            logger.debug("File name: {}, Size: {}, Content Type: {}", file.getOriginalFilename(), file.getSize(), file.getContentType());
        }

        // 파일 최대 3개 확인
        if (resignationDocuments.length > 3) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("최대 3개의 파일만 업로드할 수 있습니다.");
        }

        // 파일이 있다면 파일 업로드
        List<String> resignationDocumentsNames = new ArrayList<>();
        for (MultipartFile resignationDocument : resignationDocuments) {
            if (resignationDocument != null && !resignationDocument.isEmpty()) {
                String message = s3Service.checkFile(resignationDocument);
                if (message.equals("empty")) { // 비어있을 경우
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("파일이 비어있습니다.\n파일을 확인 후 재업로드해주세요.");
                } else if (message.equals("fail")) { // 오류가 발생했을 경우
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("파일 업로드 중 오류가 발생하였습니다.\n파일 확인 후 재업로드 또는 관리자에게 문의해주세요.");
                } else { // 성공
                    resignationDocumentsNames.add(message);
                }
            }
        }
        String message = employeeService.resignEmployee(employeeId, resignationReason, codeNumber, specificReason, resignationDate, resignationDocumentsNames);
        if (message.equals("null")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("'" + employeeId + "' 사원을 찾을 수 없습니다.");
        }
        return ResponseEntity.ok("'" + employeeId + "' 사원이 퇴사 처리되었습니다.");
    }


    @GetMapping("/resignation-detail/{employeeId}") // 사원 정보 상세 조회 페이지 이동 (퇴사 정보 포함)
    public String viewResignedEmployee(@PathVariable("employeeId") String employeeId, Model model) {
        Employee employee = employeeService.getEmployeeWithResignation(employeeId);
        if (employee == null) {
            return "error/employee-error";
        }
        String pictureUrl = s3Service.getUrl(employee.getPicture());
        model.addAttribute("pictureUrl", pictureUrl);
        model.addAttribute("employee", employee);
        return "employee/resignation-detail";
    }

    @PostMapping("/delete/{employeeId}") // 사원 영구 삭제 로직
    public ResponseEntity<String> deleteEmployee(@PathVariable("employeeId") String employeeId) {
        String message = employeeService.deleteEmployee(employeeId);
        return switch (message) {
            case "success" -> ResponseEntity.ok("사원이 삭제되었습니다.");
            case "null" -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("사원을 찾을 수 없습니다.");
            case "no_resignation" -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body("사원의 퇴사 정보가 없습니다.");
            case "not_expired" -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body("퇴사 후 1년이 지나지 않았습니다.");
            default -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("삭제하는 중 오류가 발생했습니다.");
        };
    }
    // ============================================= 퇴사 관련 로직 end-point =============================================
}
