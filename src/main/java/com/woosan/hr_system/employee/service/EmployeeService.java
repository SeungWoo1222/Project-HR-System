package com.woosan.hr_system.employee.service;

import com.woosan.hr_system.employee.model.Employee;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface EmployeeService {
    Employee getEmployeeById(String employeeId);
    Employee getEmployeeWithTermination(String employeeId);
    List<Employee> getAllEmployees();
    void insertEmployee(Employee employee);
    void updateEmployee(Employee employee);
    void updateEmployeePartial(String employeeId, Map<String, Object> updates);
    void deleteEmployee(String employeeId);
    List<Employee> getPreTerminationEmployees();
    List<Employee> getTerminatedEmployees();
    List<Employee> getPreDeletionEmployees();
    void terminateEmployee(String employeeId, String terminationReason, LocalDate terminationDate);
}
