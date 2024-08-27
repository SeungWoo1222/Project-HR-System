package com.woosan.hr_system.employee.controller;

import com.woosan.hr_system.auth.aspect.RequireHRPermission;
import com.woosan.hr_system.employee.model.Employee;
import com.woosan.hr_system.employee.service.EmployeeService;
import com.woosan.hr_system.search.PageRequest;
import com.woosan.hr_system.search.PageResult;
import com.woosan.hr_system.upload.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/employee")
public class EmployeeViewController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private FileService fileService;

    @RequireHRPermission
    @GetMapping("/list") // 모든 사원 정보 조회
    public String viewEmployees(@RequestParam(name = "page", defaultValue = "1") int page,
                               @RequestParam(name = "size", defaultValue = "10") int size,
                               @RequestParam(name = "keyword", defaultValue = "") String keyword,
                                @RequestParam(name = "department", defaultValue = "") String department,
                               Model model) {
        // 매개변수 값 로그에 출력
        PageRequest pageRequest = new PageRequest(page - 1, size, keyword); // 페이지 번호 인덱싱을 위해 다시 -1
        PageResult<Employee> pageResult = employeeService.searchEmployees(pageRequest, department);

        model.addAttribute("employees", pageResult.getData());
        model.addAttribute("currentPage", pageResult.getCurrentPage() + 1); // 뷰에서 가독성을 위해 +1
        model.addAttribute("totalPages", pageResult.getTotalPages());
        model.addAttribute("pageSize", size);
        model.addAttribute("keyword", keyword);
        model.addAttribute("department", department);

        return "employee/list";
    }

    @RequireHRPermission
    @GetMapping("/{employeeId}") // 사원 정보 상세 조회
    public String viewEmployee(@PathVariable("employeeId") String employeeId, Model model) {
        // 예외 처리된 비밀번호 정보와 퇴사 정보가 포함된 employee
        Employee employee = employeeService.getEmployeeDetails(employeeId);
        model.addAttribute("employee", employee);

        String pictureUrl = fileService.getUrl(employee.getPicture());
        model.addAttribute("pictureUrl", pictureUrl);
        return "employee/detail";
    }
    @RequireHRPermission
    @GetMapping("/list/{departmentId}")
    public List<Employee> getEmployeesByDepartment(@PathVariable("departmentId") String departmentId) {
        return employeeService.getEmployeesByDepartment(departmentId);
    }

    @RequireHRPermission
    @GetMapping("/register") // 신규 사원 등록 페이지 이동
    public String viewEmployeeForm() {
        return "employee/registration";
    }

    @RequireHRPermission
    @GetMapping("/{employeeId}/edit") // 사원 정보 수정 페이지 이동
    public String viewEmployeeEditForm(@PathVariable("employeeId") String employeeId, Model model) {
        // 예외 처리된 비밀번호 정보와 퇴사 정보가 포함된 employee
        Employee employee = employeeService.getEmployeeDetails(employeeId);
        model.addAttribute("employee", employee);

        String pictureUrl = fileService.getUrl(employee.getPicture());
        model.addAttribute("pictureUrl", pictureUrl);
        return "employee/edit";
    }

    @RequireHRPermission
    @GetMapping("/edit/resignation/{employeeId}") // 사원 퇴사 정보 수정 페이지 이동
    public String viewResignedEmployeeEditForm(@PathVariable("employeeId") String employeeId, Model model) {
        // 예외 처리된 비밀번호 정보와 퇴사 정보가 포함된 employee
        Employee employee = employeeService.getEmployeeDetails(employeeId);
        model.addAttribute("employee", employee);

        String pictureUrl = fileService.getUrl(employee.getPicture());
        model.addAttribute("pictureUrl", pictureUrl);
        return "employee/edit/resignation";
    }
}
