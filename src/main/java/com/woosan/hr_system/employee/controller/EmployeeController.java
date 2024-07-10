package com.woosan.hr_system.employee.controller;

import com.woosan.hr_system.employee.model.Employee;
import com.woosan.hr_system.employee.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @GetMapping // 모든 사원 정보 조회
    public String listEmployees(Model model) {
        List<Employee> employees = employeeService.getAllEmployees();
        model.addAttribute("employees", employees);
        return "employee/list";
    }

    @GetMapping("/{employeeId}") // 특정 사원 정보 조회
    public String getEmployee(@PathVariable String employeeId, Model model) {
        Employee employee = employeeService.getEmployeeById(employeeId);
        model.addAttribute("employee", employee);
        return "employee/view";
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
    public String editEmployeeForm(@PathVariable String employeeId, Model model) {
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
    public String updateEmployeePartial(@PathVariable String employeeId, @RequestBody Map<String, Object> updates) {
      employeeService.updateEmployeePartial(employeeId, updates);
      return "redirect:/employee/edit";
    }

    @GetMapping("/delete/{employeeId}") // 사원 정보 영구 삭제
    public String deleteEmployee(@PathVariable String employeeId) {
        employeeService.deleteEmployee(employeeId);
        return "redirect:/employee";
    }

}
