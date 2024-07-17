package com.woosan.hr_system.employee.service;

import com.woosan.hr_system.employee.model.Employee;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface EmployeeService {
    Employee getEmployeeById(String employeeId);
    Employee getEmployeeWithResignation(String employeeId);
    List<Employee> getAllEmployees();
    void insertEmployee(Employee employee);
    void updateEmployee(Employee employee);
    void updateEmployeePartial(String employeeId, Map<String, Object> updates);
    String deleteEmployee(String employeeId);
    List<Employee> getPreResignationEmployees();
    List<Employee> getResignedEmployees();
    List<Employee> getPreDeletionEmployees();
    void resignEmployee(String employeeId, String resignationReason, LocalDate resignationDate);
}
