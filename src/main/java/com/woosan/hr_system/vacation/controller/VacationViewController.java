package com.woosan.hr_system.vacation.controller;

import com.woosan.hr_system.aspect.RequireManagerPermission;
import com.woosan.hr_system.auth.service.AuthService;
import com.woosan.hr_system.employee.dao.EmployeeDAO;
import com.woosan.hr_system.holiday.service.HolidayService;
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
    private HolidayService holidayService;
    @Autowired
    private EmployeeDAO employeeDAO;

    @GetMapping("/list") // 모든 휴가 조회
    public String viewVacationList(@RequestParam(name = "page", defaultValue = "1") int page,
                                   @RequestParam(name = "size", defaultValue = "10") int size,
                                   @RequestParam(name = "keyword", defaultValue = "") String keyword,
                                   @RequestParam(name = "department", defaultValue = "") String department,
                                   @RequestParam(name = "status", defaultValue = "") String status,
                                   @RequestParam(name = "startDate", defaultValue = "") String startDate,
                                   @RequestParam(name = "endDate", defaultValue = "") String endDate,
                                   Model model) {
        PageRequest pageRequest = new PageRequest(page - 1, size, keyword); // 페이지 번호 인덱싱을 위해 다시 -1
        PageResult<Vacation> pageResult = vacationService.searchVacation(pageRequest, department, status, startDate, endDate);

        model.addAttribute("vacationList", pageResult.getData());
        model.addAttribute("currentPage", pageResult.getCurrentPage() + 1); // 뷰에서 가독성을 위해 +1
        model.addAttribute("totalPages", pageResult.getTotalPages());
        model.addAttribute("pageSize", size);
        model.addAttribute("keyword", keyword);
        model.addAttribute("department", department);
        model.addAttribute("status", status);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);

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

    @GetMapping("/{vacationId}/process") // 휴가 정보 상세 조회 (처리 기능 추가)
    public String viewVacationProcess(@PathVariable("vacationId") int vacationId, Model model) {
        Vacation vacationInfo = vacationService.getVacationById(vacationId);
        model.addAttribute("vacationInfo", vacationInfo);
        model.addAttribute("employeeInfo",
                employeeDAO.getEmployeeById(vacationInfo.getEmployeeId()));
        return "/vacation/detail-process";
    }

    @GetMapping("/employee") // 내 휴가 내역 조회
    public String viewEmployeeVacationInfo(@RequestParam(name = "page", defaultValue = "1") int page,
                                           @RequestParam(name = "size", defaultValue = "10") int size,
                                           @RequestParam(name = "startDate", defaultValue = "") String startDate,
                                           @RequestParam(name = "endDate", defaultValue = "") String endDate,
                                           Model model) {
        String employeeId = authService.getAuthenticatedUser().getUsername();
        model.addAttribute("remainingLeave", employeeDAO.getEmployeeById(employeeId).getRemainingLeave());

        PageRequest pageRequest = new PageRequest(page - 1, size); // 페이지 번호 인덱싱을 위해 다시 -1
        PageResult<Vacation> pageResult = vacationService.getVacationsByEmployeeId(pageRequest, employeeId, startDate, endDate);

        model.addAttribute("vacationList", pageResult.getData());
        model.addAttribute("currentPage", pageResult.getCurrentPage() + 1); // 뷰에서 가독성을 위해 +1
        model.addAttribute("totalPages", pageResult.getTotalPages());
        model.addAttribute("pageSize", size);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);

        return "/vacation/employee";
    }

    @RequireManagerPermission
    @GetMapping("/department") // 해당 부서의 모든 휴가 정보 조회
    public String viewDepartmentVacationInfo(@RequestParam(name = "page", defaultValue = "1") int page,
                                             @RequestParam(name = "size", defaultValue = "10") int size,
                                             @RequestParam(name = "status", defaultValue = "") String status,
                                             @RequestParam(name = "startDate", defaultValue = "") String startDate,
                                             @RequestParam(name = "endDate", defaultValue = "") String endDate,
                                             Model model) {
        PageRequest pageRequest = new PageRequest(page - 1, size); // 페이지 번호 인덱싱을 위해 다시 -1
        PageResult<Vacation> pageResult = vacationService.getVacationsByDepartmentId(pageRequest, authService.getAuthenticatedUser().getDepartment(), status, startDate, endDate);

        model.addAttribute("vacationList", pageResult.getData());
        model.addAttribute("currentPage", pageResult.getCurrentPage() + 1); // 뷰에서 가독성을 위해 +1
        model.addAttribute("totalPages", pageResult.getTotalPages());
        model.addAttribute("pageSize", size);
        model.addAttribute("status", status);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);

        return "/vacation/department";
    }

    @GetMapping("/{employeeId}/request") // 내 휴가 신청 페이지 이동
    public String viewMyRequestForm(@PathVariable("employeeId") String employeeId,
                                  Model model) {
        // 로그인한 사원 정보 모델에 추가
        model.addAttribute("employee", employeeDAO.getEmployeeById(employeeId));

        // 모든 공휴일 모델에 추가
        model.addAttribute("holidays", holidayService.getAllHoliday());
        return "/vacation/request";
    }

    @GetMapping("/request") // 휴가 신청 페이지 이동
    public String viewRequestForm(Model model) {
        // 모든 사원 정보 모델에 추가
        model.addAttribute("employeeList", employeeDAO.getAllEmployees());

        // 모든 공휴일 모델에 추가
        model.addAttribute("holidays", holidayService.getAllHoliday());
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
