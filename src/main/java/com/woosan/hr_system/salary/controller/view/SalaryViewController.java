package com.woosan.hr_system.salary.controller.view;

import com.woosan.hr_system.auth.aspect.RequireHRPermission;
import com.woosan.hr_system.salary.service.SalaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/salary")
public class SalaryViewController {
    @Autowired
    private SalaryService salaryService;

    // 모든 급여 정보 페이지 이동
    @RequireHRPermission
    @GetMapping("/list")
    private String viewAllSalaries(Model model) {
        model.addAttribute("salaryList", salaryService.getAllSalaries());
        return "/salary/list";
    }

    // 급여 정보 수정 페이지 이동
    @RequireHRPermission
    @GetMapping("/{salaryId}/edit")
    private String viewSalaryEditForm(@PathVariable int salaryId, Model model) {
        model.addAttribute("salary", salaryService.getSalaryById(salaryId));
        return "/salary/edit";
    }
}
