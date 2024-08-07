package com.woosan.hr_system.employee.service;

import com.woosan.hr_system.employee.model.Employee;
import com.woosan.hr_system.employee.model.Resignation;
import com.woosan.hr_system.search.PageRequest;
import com.woosan.hr_system.search.PageResult;

import java.util.List;

public interface EmployeeService {
    PageResult<Employee> searchEmployees(PageRequest pageRequest);
    Employee getEmployeeById(String employeeId);
    Employee getEmployeeWithAdditionalInfo(String employeeId);
    void populateEmployeeDetails(Employee employee);
    List<Employee> getPreResignationEmployees();
    List<Employee> getResignedEmployees();
    List<Employee> getPreDeletionEmployees();
    void updateResignationInfo(String employeeId, Resignation resignation);
    void updateResignationDocuments(Resignation resignation, String newDocumentsName);
    String insertEmployee(Employee employee);
    String updateEmployee(Employee employee);
    String resignEmployee(String employeeId, Resignation resignation);
    String deleteEmployee(String employeeId);
    String setAccountLock(String employeeId);
    String updateStatus(String employeeId, String status);
    String promoteEmployee(String employeeId);
}
