package com.woosan.hr_system.employee.service;

import com.woosan.hr_system.employee.model.Employee;
import com.woosan.hr_system.resignation.model.Resignation;
import com.woosan.hr_system.search.PageRequest;
import com.woosan.hr_system.search.PageResult;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface EmployeeService {
    // == 조회 ==
    Employee getEmployeeById(String employeeId);
    String getEmployeeNameById(String employeeId);
    Employee getEmployeeDetails(String employeeId);
    PageResult<Employee> searchEmployees(PageRequest pageRequest);
    List<Employee> getPreResignationEmployees();
    List<Employee> getResignedEmployees();
    List<Employee> getPreDeletionEmployees();
    List<Employee> getEmployeesByDepartment(String departmentId);

    // == 등록 ==
    String insertEmployee(Employee employee);

    // == 수정 ==
    String updateEmployee(Employee employee);
    String updateStatus(String employeeId, String status);
    String promoteEmployee(String employeeId);

    // == 삭제 ==
    String deleteEmployee(String employeeId);

    // == 기타 ==
    void updateResignationDocuments(Resignation resignation, String newDocumentsName);
    void assignPictureFromUpload(Employee employee, MultipartFile file);
}
