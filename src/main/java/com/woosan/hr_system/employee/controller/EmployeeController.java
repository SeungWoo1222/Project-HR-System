package com.woosan.hr_system.employee.controller;

import com.woosan.hr_system.auth.CustomUserDetails;
import com.woosan.hr_system.employee.model.Employee;
import com.woosan.hr_system.employee.model.Termination;
import com.woosan.hr_system.employee.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

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
    public String viewEmployee(@PathVariable("employeeId") String employeeId, Model model) {
        Employee employee = employeeService.getEmployeeById(employeeId);
        if (employee == null) {
            return "error/404";
        }
        model.addAttribute("employee", employee);
        return "employee/detail";
    }

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

    @PostMapping("/delete/{employeeId}") // 사원 정보 영구 삭제 로직
    public String deleteEmployee(@PathVariable("employeeId") String employeeId) {
        employeeService.deleteEmployee(employeeId);
        return "redirect:/employee/terminate";
    }

    @GetMapping("/terminate") // 사원 퇴사 관리 페이지 이동
    public String terminateEmployees(Model model) {
        List<Employee> preTerminationEmployees = employeeService.getPreTerminationEmployees();
        List<Employee> terminatedEmployees = employeeService.getTerminatedEmployees();
        List<Employee> preDeletionEmployees = employeeService.getPreDeletionEmployees();
        model.addAttribute("preTerminationEmployees", preTerminationEmployees);
        model.addAttribute("terminatedEmployees", terminatedEmployees);
        model.addAttribute("preDeletionEmployees", preDeletionEmployees);
        return "employee/terminate";
    }

    @PostMapping("/terminate/{employeeId}") // 사원 퇴사 처리 로직
    public String terminateEmployee(@PathVariable("employeeId") String employeeId,
                                    @RequestParam("terminationReason") String terminationReason,
                                    @RequestParam("terminationDate") LocalDate terminationDate) {
        employeeService.terminateEmployee(employeeId, terminationReason, terminationDate);
        return "redirect:/employee/terminate";
    }

    @GetMapping("/resignation-form/{employeeId}") // 사원 퇴사 처리 폼 페이지 이동
    public String viewEmployeeForTermination(@PathVariable("employeeId") String employeeId, Model model) {
        Employee employee = employeeService.getEmployeeById(employeeId);
        if (employee == null) {
            return "error/404";
        }
        model.addAttribute("employee", employee);
        return "employee/termination-form";
    }

    @GetMapping("/termination-detail/{employeeId}") // 사원 정보 상세 조회 페이지 이동 (퇴사 정보 포함)
    public String viewTerminatedEmployee(@PathVariable("employeeId") String employeeId, Model model) {
        Employee employee = employeeService.getEmployeeWithTermination(employeeId);
        if (employee == null) {
            return "error/404";
        }
        model.addAttribute("employee", employee);
        return "employee/termination-detail";
    }
}
