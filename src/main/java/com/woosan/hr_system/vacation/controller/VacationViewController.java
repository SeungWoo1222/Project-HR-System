package com.woosan.hr_system.vacation.controller;

import com.woosan.hr_system.vacation.service.VacationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/vacation")
public class VacationViewController {
    @Autowired
    private VacationService vacationService;

    @GetMapping("/{vacationId}") // 휴가 정보 상세 조회
    public String viewVacationInfo(@PathVariable int vacationId, Model model) {
        model.addAttribute("vacationInfo", vacationService.getVacationById(vacationId));
        return "/vacation/detail";
    }

    @GetMapping("/{employeeId}") // 해당 사원의 모든 휴가 정보 조회
    public String viewEmployeeVacationInfo(@PathVariable String employeeId, Model model) {
        model.addAttribute("vacationList", vacationService.getVacationByEmployeeId(employeeId));
        return "/vacation/employee";
    }

    @GetMapping("/{departmentId}") // 해당 부서의 모든 휴가 정보 조회
    public String viewDepartmentVacationInfo(@PathVariable String departmentId, Model model) {
        model.addAttribute("vacationList", vacationService.getVacationByDepartmentId(departmentId));
        return "/vacation/department";
    }

    @GetMapping("/request") // 휴가 신청 페이지 이동
    public String viewRequestForm() {
        return "/vacation/request";
    }

    @GetMapping("/{vacationId}/edit") // 휴가 수정 페이지 이동
    public String viewEditForm(@PathVariable("vacationId") int vacationId, Model model) {
        model.addAttribute("vacationInfo", vacationService.getVacationById(vacationId));
        return "/vacation/edit";
    }
}
