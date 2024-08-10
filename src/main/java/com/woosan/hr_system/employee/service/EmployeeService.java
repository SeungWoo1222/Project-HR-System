package com.woosan.hr_system.employee.service;

import com.woosan.hr_system.employee.model.Employee;
import com.woosan.hr_system.employee.model.Resignation;
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

    // == 등록 ==
    String insertEmployee(Employee employee);
    String resignEmployee(String employeeId, Resignation resignation);

    // == 수정 ==
    String updateEmployee(Employee employee);
    String updateStatus(String employeeId, String status);
    String setAccountLock(String employeeId);
    String promoteEmployee(String employeeId);
    String updateResignationInfo(String employeeId, Resignation resignation);

    // == 삭제 ==
    String deleteEmployee(String employeeId);

    // == 기타 ==
    void updateResignationDocuments(Resignation resignation, String newDocumentsName);
    void assignPictureFromUpload(Employee employee, MultipartFile file);
}
