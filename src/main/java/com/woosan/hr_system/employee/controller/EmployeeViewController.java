package com.woosan.hr_system.employee.controller;

import com.woosan.hr_system.auth.dao.PasswordDAO;
import com.woosan.hr_system.auth.model.Password;
import com.woosan.hr_system.auth.service.AuthService;
import com.woosan.hr_system.employee.dao.ResignationDAO;
import com.woosan.hr_system.employee.model.Employee;
import com.woosan.hr_system.employee.model.Resignation;
import com.woosan.hr_system.employee.service.EmployeeService;
import com.woosan.hr_system.search.PageRequest;
import com.woosan.hr_system.search.PageResult;
import com.woosan.hr_system.upload.FileService;
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
@RequestMapping("/employee")
public class EmployeeViewController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private AuthService authService;

    @Autowired
    private FileService fileService;
    @Autowired
    private ResignationDAO resignationDAO;
    @Autowired
    private PasswordDAO passwordDAO;

    // ============================================ 조회 관련 로직 start-point ============================================
    @GetMapping("/list") // 모든 사원 정보 조회
    public String viewEmployees(@RequestParam(name = "page", defaultValue = "1") int page,
                               @RequestParam(name = "size", defaultValue = "10") int size,
                               @RequestParam(name = "keyword", defaultValue = "") String keyword,
                               Model model) {
        // 매개변수 값 로그에 출력
        PageRequest pageRequest = new PageRequest(page - 1, size, keyword); // 페이지 번호 인덱싱을 위해 다시 -1
        PageResult<Employee> pageResult = employeeService.searchEmployees(pageRequest);

        model.addAttribute("employees", pageResult.getData());
        model.addAttribute("currentPage", pageResult.getCurrentPage() + 1); // 뷰에서 가독성을 위해 +1
        model.addAttribute("totalPages", pageResult.getTotalPages());
        model.addAttribute("pageSize", size);
        model.addAttribute("keyword", keyword);
        return "employee/list";
    }

    @GetMapping("/{employeeId}") // 사원 정보 상세 조회
    public String viewEmployee(@PathVariable("employeeId") String employeeId, Model model) {
        Employee employee = employeeService.getEmployeeWithAdditionalInfo(employeeId);
        if (employee == null) {
            return "error/404";
        }
        // 비밀번호 정보와 퇴사 정보 설정
        populateEmployeeDetails(employee);

        String pictureUrl = fileService.getUrl(employee.getPicture());
        model.addAttribute("pictureUrl", pictureUrl);
        model.addAttribute("employee", employee);
        return "employee/detail";
    }

    @GetMapping("/myInfo") // 내 정보 조회
    public String viewMyInfo(Model model) {
        String employeeId = authService.getAuthenticatedUser().getUsername();
        Employee employee = employeeService.getEmployeeById(employeeId);
        if (employee == null) {
            return "error/404";
        }
        String pictureUrl = fileService.getUrl(employee.getPicture());
        model.addAttribute("pictureUrl", pictureUrl);
        model.addAttribute("employee", employee);
        return "employee/myInfo";
    }
    // ============================================= 조회 관련 로직 end-point =============================================

    // ============================================ 등록 관련 로직 start-point ============================================
    @GetMapping("/registration") // 신규 사원 등록 페이지 이동
    public String viewEmployeeForm() {
        return "employee/registration";
    }
    // ============================================= 등록 관련 로직 end-point =============================================

    // ============================================ 수정 관련 로직 start-point ============================================
    @GetMapping("/edit/myInfo/{employeeId}") // 내 정보 수정 페이지 이동
    public String viewMyInfoEditForm(@PathVariable("employeeId") String employeeId, Model model) {
        Employee employee = employeeService.getEmployeeById(employeeId);
        String pictureUrl = fileService.getUrl(employee.getPicture());
        model.addAttribute("pictureUrl", pictureUrl);
        model.addAttribute("employee", employee);
        return "employee/edit/myInfo";
    }

    @GetMapping("/edit/detail/{employeeId}") // 사원 정보 수정 페이지 이동
    public String viewEmployeeEditForm(@PathVariable("employeeId") String employeeId, Model model) {
        Employee employee = employeeService.getEmployeeById(employeeId);

        // 비밀번호 정보와 퇴사 정보 설정
        populateEmployeeDetails(employee);

        String pictureUrl = fileService.getUrl(employee.getPicture());
        model.addAttribute("pictureUrl", pictureUrl);
        model.addAttribute("employee", employee);
        return "employee/edit/detail";
    }

    @GetMapping("/edit/resignation/{employeeId}") // 사원 퇴사 정보 수정 페이지 이동
    public String viewResignedEmployeeEditForm(@PathVariable("employeeId") String employeeId, Model model) {
        Employee employee = employeeService.getEmployeeById(employeeId);

        // 비밀번호 정보와 퇴사 정보 설정
        populateEmployeeDetails(employee);
        model.addAttribute("employee", employee);

        String pictureUrl = fileService.getUrl(employee.getPicture());
        model.addAttribute("pictureUrl", pictureUrl);

        return "employee/edit/resignation";
    }
    // ============================================= 수정 관련 로직 end-point =============================================

    // ============================================ 퇴사 관련 로직 start-point ============================================
    @GetMapping("/resignation") // 사원 퇴사 관리 페이지 이동
    public String viewResignationManagement(Model model) {
        List<Employee> preResignationEmployees = employeeService.getPreResignationEmployees();
        List<Employee> resignedEmployees = employeeService.getResignedEmployees();
        List<Employee> preDeletionEmployees = employeeService.getPreDeletionEmployees();
        model.addAttribute("preResignationEmployees", preResignationEmployees);
        model.addAttribute("resignedEmployees", resignedEmployees);
        model.addAttribute("preDeletionEmployees", preDeletionEmployees);
        return "/employee/resignation";
    }

    @GetMapping("/resignation-form/{employeeId}") // 사원 퇴사 처리 폼 페이지 이동
    public String viewEmployeeForResignation(@PathVariable("employeeId") String employeeId, Model model) {
        Employee employee = employeeService.getEmployeeById(employeeId);
        if (employee == null) {
            return "error/employee-error";
        }
        String pictureUrl = fileService.getUrl(employee.getPicture());
        model.addAttribute("pictureUrl", pictureUrl);
        model.addAttribute("employee", employee);
        return "employee/resignation-form";
    }
    // ============================================= 퇴사 관련 로직 end-point =============================================

    // ================================================== 기타 로직 ======================================================
    // 비밀번호 정보와 퇴사 정보 설정하는 메소드
    private void populateEmployeeDetails(Employee employee) {
        String employeeId = employee.getEmployeeId();

        // 비밀번호 정보 조회 및 설정
        Password password = passwordDAO.selectPassword(employeeId);
        employee.setPassword(password);

        // 퇴사 정보 조회 및 설정
        if (employee.getStatus().equals("퇴사")) {
            Resignation resignation = resignationDAO.getResignedEmployee(employeeId);
            employee.setResignation(resignation);
        }
    }
}
