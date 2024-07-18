package com.woosan.hr_system.employee.controller;

import com.woosan.hr_system.Search.PageRequest;
import com.woosan.hr_system.Search.PageResult;
import com.woosan.hr_system.employee.model.Employee;
import com.woosan.hr_system.employee.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    // 조회 관련 로직 start-point
    @GetMapping("/list") // 모든 사원 정보 조회
    public String getEmployees(@RequestParam(name = "page", defaultValue = "0") int page,
                               @RequestParam(name = "size", defaultValue = "10") int size,
                               @RequestParam(name = "keyword", required = false) String keyword,
                               Model model) {
        PageRequest pageRequest = new PageRequest(page, size, keyword);
        PageResult<Employee> pageResult = employeeService.searchEmployees(pageRequest);
        model.addAttribute("employees", pageResult.getData());
        model.addAttribute("currentPage", pageResult.getCurrentPage());
        model.addAttribute("totalPages", pageResult.getTotalPages());
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

//    @GetMapping("/detail/{employeeId}") // 사원 정보 상세 조회
//    public String viewEmployeeDetail(@PathVariable("employeeId") String employeeId, Model model) {
//        Employee employee = employeeService.getEmployeeById(employeeId);
//        if (employee == null) {
//            return "error/404";
//        }
//        model.addAttribute("employee", employee);
//        return "employee/detail";
//    }
    // 조회 관련 로직 end-point

    // 등록 관련 로직 start-point
    @GetMapping("/registration") // 신규 사원 등록 페이지 이동
    public String newEmployeeForm(Model model) {
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

    @PostMapping("/resign/{employeeId}") // 사원 퇴사 처리 로직
    public ResponseEntity<String> resignEmployee(@PathVariable("employeeId") String employeeId,
                                    @RequestParam("resignationReason") String resignationReason,
                                    @RequestParam("codeNumber") String codeNumber,
                                    @RequestParam("specificReason") String specificReason,
                                    @RequestParam("resignationDate") LocalDate resignationDate) {
        String message = employeeService.resignEmployee(employeeId, resignationReason, codeNumber, specificReason, resignationDate);
        if ("null".equals(message)) {
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
