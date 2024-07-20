package com.woosan.hr_system.employee.controller;

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
import java.util.Map;

@Controller
@RequestMapping("/employee")
public class EmployeeController {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeController.class);

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private S3Service s3Service;

    // 조회 관련 로직 start-point
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
        model.addAttribute("employee", employee);
        return "employee/detail";
    }
    // 조회 관련 로직 end-point

    // 등록 관련 로직 start-point
    @GetMapping("/registration") // 신규 사원 등록 페이지 이동
    public String viewEmployeeForm(Model model) {
        model.addAttribute("employee", new Employee());
        return "employee/registration";
    }

    @PostMapping("/registration") // 신규 사원 등록
    public ResponseEntity<String> registerEmployee(@ModelAttribute Employee employee) {
        String message = employeeService.insertEmployee(employee);
        if (message.equals("fail")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("입력 정보에서 오류가 발생하였습니다.");
        }
        return ResponseEntity.ok( "'" + employee.getName() + "' 사원이 신규 사원으로 등록되었습니다.");
    }
    // 등록 관련 로직 end-point

    // 수정 관련 로직 start-point
    @GetMapping("/edit/{employeeId}") // 사원 정보 수정 페이지 이동
    public String editEmployeeForm(@PathVariable("employeeId") String employeeId, Model model) {
        Employee employee = employeeService.getEmployeeById(employeeId);
        model.addAttribute("employee", employee);
        return "employee/edit";
    }

    @PostMapping("/update") // 사원 정보 전체 수정
    public String updateEmployee(@ModelAttribute Employee employee) {
        employeeService.updateEmployee(employee);
        return "redirect:/employee/edit";
    }

    @PatchMapping("/{employeeId}") // 사원 정보 일부 수정
    public String updateEmployeePartial(@PathVariable("employeeId") String employeeId, @RequestBody Map<String, Object> updates) {
        employeeService.updateEmployeePartial(employeeId, updates);
        return "redirect:/employee/edit";
    }
    // 수정 관련 로직 end-point

    // 퇴사 관련 로직 start-point
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
                String fileUploadMessage = s3Service.checkFile(resignationDocument);
                if (fileUploadMessage.equals("empty")) { // 비어있을 경우
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("파일이 비어있습니다.\n파일을 확인 후 재업로드해주세요.");
                } else if (fileUploadMessage.equals("fail")) { // 오류가 발생했을 경우
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("파일 업로드 중 오류가 발생하였습니다.\n파일 확인 후 재업로드 또는 관리자에게 문의해주세요.");
                } else { // 성공
                    resignationDocumentsNames.add(fileUploadMessage);
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
        model.addAttribute("employee", employee);
        return "employee/resignation-detail";
    }

    @PostMapping("/delete/{employeeId}") // 사원 영구 삭제 로직
    @ResponseBody
    public ResponseEntity<String> deleteEmployee(@PathVariable("employeeId") String employeeId) {
        String message = employeeService.deleteEmployee(employeeId);
        switch (message) {
            case "success":
                return ResponseEntity.ok("사원이 삭제되었습니다.");
            case "null":
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사원을 찾을 수 없습니다.");
            case "no_resignation":
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("사원의 퇴사 정보가 없습니다.");
            case "not_expired":
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("퇴사 후 1년이 지나지 않았습니다.");
            default:
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("삭제하는 중 오류가 발생했습니다.");
        }
    }
    // 퇴사 관련 로직 end-point
}
