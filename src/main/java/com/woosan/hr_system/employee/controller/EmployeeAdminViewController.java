package com.woosan.hr_system.employee.controller;

import com.woosan.hr_system.employee.model.Employee;
import com.woosan.hr_system.employee.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/admin/employee")
public class EmployeeAdminViewController {
    @Autowired
    private EmployeeService employeeService;

    // 재직 상태 수정
    @GetMapping("/edit/status/{employeeId}")
    public String employeeStatus(@PathVariable("employeeId") String employeeId, Model model) {
        Employee employee = employeeService.getEmployeeById(employeeId);
        String status = employee.getStatus();
        String name = employee.getName();
        model.addAttribute("status", status);
        model.addAttribute("name", name);
        model.addAttribute("employeeId", employeeId);
        return "/admin/employee/edit/status";
    }
}
