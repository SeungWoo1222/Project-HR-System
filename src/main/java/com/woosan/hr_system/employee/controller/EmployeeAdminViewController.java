package com.woosan.hr_system.employee.controller;

import com.woosan.hr_system.aspect.RequireHRPermission;
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
    @RequireHRPermission
    @GetMapping("/{employeeId}/status/edit")
    public String employeeStatus(@PathVariable("employeeId") String employeeId, Model model) {
        model.addAttribute("employee", employeeService.getEmployeeById(employeeId));
        return "employee/status";
    }
}
