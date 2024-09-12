package com.woosan.hr_system.vacation.controller;

import com.woosan.hr_system.aspect.RequireManagerPermission;
import com.woosan.hr_system.auth.service.AuthService;
import com.woosan.hr_system.employee.dao.EmployeeDAO;
import com.woosan.hr_system.search.PageRequest;
import com.woosan.hr_system.search.PageResult;
import com.woosan.hr_system.vacation.model.Vacation;
import com.woosan.hr_system.vacation.service.VacationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
@RequestMapping("/vacation")
public class VacationViewController {
    @Autowired
    private VacationService vacationService;
    @Autowired
    private AuthService authService;
    @Autowired
    private EmployeeDAO employeeDAO;

    @GetMapping("/list") // 모든 휴가 조회
    public String viewVacationList(Model model) {
        return "vacation/list";
    }

    @GetMapping("/{vacationId}") // 휴가 정보 상세 조회
    public String viewVacationInfo(@PathVariable("vacationId") int vacationId, Model model) {
        Vacation vacationInfo = vacationService.getVacationById(vacationId);
        model.addAttribute("vacationInfo", vacationInfo);
        model.addAttribute("employeeInfo",
                employeeDAO.getEmployeeById(vacationInfo.getEmployeeId()));
        return "/vacation/detail";
    }

    @GetMapping("/employee") // 해당 사원의 모든 휴가 정보 조회
    public String viewEmployeeVacationInfo(@RequestParam(name = "page", defaultValue = "1") int page,
                                           @RequestParam(name = "size", defaultValue = "10") int size,
                                           Model model) {
        String employeeId = authService.getAuthenticatedUser().getUsername();
        model.addAttribute("remainingLeave", employeeDAO.getEmployeeById(employeeId).getRemainingLeave());

        PageRequest pageRequest = new PageRequest(page - 1, size); // 페이지 번호 인덱싱을 위해 다시 -1
        PageResult<Vacation> pageResult = vacationService.getVacationByEmployeeId
                (pageRequest, employeeId);

        model.addAttribute("vacationList", pageResult.getData());
        model.addAttribute("currentPage", pageResult.getCurrentPage() + 1); // 뷰에서 가독성을 위해 +1
        model.addAttribute("totalPages", pageResult.getTotalPages());
        model.addAttribute("pageSize", size);


        return "/vacation/employee";
    }

    @RequireManagerPermission
    @GetMapping("/department") // 해당 부서의 모든 휴가 정보 조회
    public String viewDepartmentVacationInfo(Model model) {
        model.addAttribute("vacationList",
                vacationService.getVacationByDepartmentId(authService.getAuthenticatedUser().getDepartment()));
        return "/vacation/department";
    }

    @GetMapping("/{employeeId}/request") // 내 휴가 신청 페이지 이동
    public String viewMyRequestForm(@PathVariable("employeeId") String employeeId,
                                  Model model) {
        model.addAttribute("employee", employeeDAO.getEmployeeById(employeeId));
        return "/vacation/request";
    }

    @GetMapping("/request") // 휴가 신청 페이지 이동
    public String viewRequestForm(Model model) {
        model.addAttribute("employeeList", employeeDAO.getAllEmployees());
        return "/vacation/request2";
    }

    @GetMapping("/{vacationId}/edit") // 휴가 수정 페이지 이동
    public String viewEditForm(@PathVariable("vacationId") int vacationId, Model model) {
        Vacation vacationInfo = vacationService.getVacationById(vacationId);
        model.addAttribute("vacationInfo", vacationInfo);
        model.addAttribute("employeeInfo",
                employeeDAO.getEmployeeById(vacationInfo.getEmployeeId()));
        return "/vacation/edit";
    }
}
