package com.woosan.hr_system.employee.service;

import com.woosan.hr_system.search.PageRequest;
import com.woosan.hr_system.search.PageResult;
import com.woosan.hr_system.employee.model.Employee;

import java.time.LocalDate;
import java.util.List;

public interface EmployeeService {
    PageResult<Employee> searchEmployees(PageRequest pageRequest);
    Employee getEmployeeById(String employeeId);
    Employee getEmployeeWithAdditionalInfo(String employeeId);
    List<Employee> getPreResignationEmployees();
    List<Employee> getResignedEmployees();
    List<Employee> getPreDeletionEmployees();
    String insertEmployee(Employee employee);
    String updateEmployee(Employee employee);
    String resignEmployee(String employeeId, String resignationReason, String codeNumber, String specificReason, LocalDate resignationDate, List<String> resignationDocumentsName);
    String deleteEmployee(String employeeId);
}
