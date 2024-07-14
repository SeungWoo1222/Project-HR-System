package com.woosan.hr_system.employee.service;

import com.woosan.hr_system.employee.model.Employee;
import java.util.List;
import java.util.Map;

public interface EmployeeService {
    Employee getEmployeeById(String employeeId);
    List<Employee> getAllEmployees();
    void insertEmployee(Employee employee);
    void updateEmployee(Employee employee);
    void updateEmployeePartial(String employeeId, Map<String, Object> updates);
    List<Employee> getTerminatedEmployees();
    void deleteEmployee(String employeeId);
}
