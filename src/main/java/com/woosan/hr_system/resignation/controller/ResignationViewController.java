package com.woosan.hr_system.resignation.controller;

import com.woosan.hr_system.auth.aspect.RequireHRPermission;
import com.woosan.hr_system.employee.model.Employee;
import com.woosan.hr_system.employee.service.EmployeeService;
import com.woosan.hr_system.resignation.service.ResignationService;
import com.woosan.hr_system.upload.model.File;
import com.woosan.hr_system.upload.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/resignation")
public class ResignationViewController {
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private ResignationService resignationService;
    @Autowired
    private FileService fileService;

    @RequireHRPermission
    @GetMapping("/management") // 퇴사 관리 페이지 이동
    public String viewResignationManagement(Model model) {
        List<Employee> preResignationEmployees = employeeService.getPreResignationEmployees();
        List<Employee> resignedEmployees = employeeService.getResignedEmployees();
        List<Employee> preDeletionEmployees = employeeService.getPreDeletionEmployees();
        model.addAttribute("preResignationEmployees", preResignationEmployees);
        model.addAttribute("resignedEmployees", resignedEmployees);
        model.addAttribute("preDeletionEmployees", preDeletionEmployees);
        return "resignation/management";
    }

    @RequireHRPermission
    @GetMapping("/{employeeId}") // 퇴사 처리 폼 페이지 이동
    public String viewEmployeeForResignation(@PathVariable("employeeId") String employeeId, Model model) {
        // 예외 처리된 기본 employee
        Employee employee = employeeService.getEmployeeById(employeeId);
        model.addAttribute("employee", employee);

        String pictureUrl = fileService.getUrl(employee.getPicture());
        model.addAttribute("pictureUrl", pictureUrl);
        return "resignation/process";
    }

    @RequireHRPermission
    @GetMapping("/{employeeId}/edit") // 퇴사 정보 수정 페이지 이동
    public String viewResignedEmployeeEditForm(@PathVariable("employeeId") String employeeId, Model model) {
        // 예외 처리된 비밀번호 정보와 퇴사 정보가 포함된 employee
        Employee employee = employeeService.getEmployeeDetails(employeeId);
        model.addAttribute("employee", employee);

        // 파일 정보 조회
        List<File> fileList = resignationService.getAllFileInfo(employeeId);
        model.addAttribute("fileList", fileList);

        String pictureUrl = fileService.getUrl(employee.getPicture());
        model.addAttribute("pictureUrl", pictureUrl);
        return "resignation/edit";
    }
}
