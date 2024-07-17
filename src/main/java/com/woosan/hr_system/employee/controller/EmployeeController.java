package com.woosan.hr_system.employee.controller;

import com.woosan.hr_system.employee.model.Employee;
import com.woosan.hr_system.employee.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public String listEmployees(Model model) {
        List<Employee> employees = employeeService.getAllEmployees();
        model.addAttribute("employees", employees);
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
    @GetMapping("/detail/{employeeId}") // 사원 정보 상세 조회
    public String viewEmployeeDetail(@PathVariable("employeeId") String employeeId, Model model) {
        Employee employee = employeeService.getEmployeeById(employeeId);
        if (employee == null) {
            return "error/404";
        }
        model.addAttribute("employee", employee);
        return "employee/detail";
    }
    // 조회 관련 로직 end-point

    // 등록 관련 로직 start-point
    @GetMapping("/register") // 신규 사원 등록 페이지 이동
    public String newEmployeeForm(Model model) {
        model.addAttribute("employee", new Employee());
        return "employee/register";
    }

    @PostMapping("/register") // 신규 사원 등록
    public String registerEmployee(@ModelAttribute Employee employee) {
        employeeService.insertEmployee(employee);
        return "redirect:/employee/register";
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
    public String resignEmployee(@PathVariable("employeeId") String employeeId,
                                    @RequestParam("resignationReason") String resignationReason,
                                    @RequestParam("resignationDate") LocalDate resignationDate) {
        employeeService.resignEmployee(employeeId, resignationReason, resignationDate);
        return "redirect:/employee/resignation";
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

    @PostMapping("/delete/{employeeId}") // 사원 정보 영구 삭제 로직
    public String deleteEmployee(@PathVariable("employeeId") String employeeId) {
        employeeService.deleteEmployee(employeeId);
        return "redirect:/employee/resignation";
    }
    // 퇴사 관련 로직 end-point
}
