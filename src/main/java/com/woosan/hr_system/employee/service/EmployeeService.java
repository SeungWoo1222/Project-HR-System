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
    List<Employee> getPreResignationEmployees();
    List<Employee> getResignedEmployees();
    List<Employee> getPreDeletionEmployees();
    String updateResignationInfo(String employeeId, Resignation resignation);
    void updateResignationDocuments(Resignation resignation, String registeredResignationDocuments);
    String insertEmployee(Employee employee);
    String updateEmployee(Employee employee);
    String resignEmployee(String employeeId, Resignation resignation);
    String deleteEmployee(String employeeId);
}
