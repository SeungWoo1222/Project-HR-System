package com.woosan.hr_system.salary.controller.view;

import com.woosan.hr_system.aspect.RequireHRPermission;
import com.woosan.hr_system.employee.dao.EmployeeDAO;
import com.woosan.hr_system.employee.model.Employee;
import com.woosan.hr_system.salary.model.Salary;
import com.woosan.hr_system.salary.service.SalaryService;
import com.woosan.hr_system.search.PageRequest;
import com.woosan.hr_system.search.PageResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/salary")
public class SalaryViewController {
    @Autowired
    private SalaryService salaryService;
    @Autowired
    private EmployeeDAO employeeDAO;

    @RequireHRPermission
    @GetMapping("/list") // 모든 급여 정보 페이지 이동
    public String viewAllSalaries(@RequestParam(name = "page", defaultValue = "1") int page,
                                  @RequestParam(name = "size", defaultValue = "10") int size,
                                  @RequestParam(name = "keyword", defaultValue = "") String keyword,
                                  @RequestParam(name = "department", defaultValue = "") String department,
                                  Model model) {
        // 검색 후 페이징
        PageRequest pageRequest = new PageRequest(page - 1, size, keyword); // 페이지 번호 인덱싱을 위해 다시 -1
        PageResult<Salary> pageResult = salaryService.searchSalaries(pageRequest, department);

        model.addAttribute("salaries", pageResult.getData());
        model.addAttribute("currentPage", pageResult.getCurrentPage() + 1); // 뷰에서 가독성을 위해 +1
        model.addAttribute("totalPages", pageResult.getTotalPages());
        model.addAttribute("pageSize", size);
        model.addAttribute("keyword", keyword);
        model.addAttribute("department", department);

        // 급여정보 미등록 사원 목록
        List<Employee> employeesWithoutSalary = salaryService.getEmployeeList();
        model.addAttribute("employeesWithoutSalary", employeesWithoutSalary);
        return "salary/list";
    }

    @RequireHRPermission
    @GetMapping("/register") // 급여 정보 등록 페이지 이동
    public String viewSalaryRegisterForm(@RequestParam(name = "employeeId", required = false) String employeeId,
                                          Model model) {
        if (employeeId != null) {
            model.addAttribute("selectedEmployeeId", employeeId);
        }
        model.addAttribute("employeeList", employeeDAO.getAllEmployees());
        return "salary/register";
    }

    @RequireHRPermission
    @GetMapping("/{salaryId}/edit") // 급여 정보 수정 페이지 이동
    public String viewSalaryEditForm(@PathVariable int salaryId, Model model) {
        Salary salaryInfo = salaryService.getSalaryById(salaryId);
        model.addAttribute("salary", salaryInfo);
        return "salary/edit";
    }

    @GetMapping("/{employeeId}/account/edit") // 계좌 정보 수정 페이지 이동
    public String viewAccountEditForm(Model model, @PathVariable("employeeId") String employeeId) {
        model.addAttribute("salaryInfo", salaryService.getSalaryByEmployeeId(employeeId));
        return "salary/account-edit";
    }
}
