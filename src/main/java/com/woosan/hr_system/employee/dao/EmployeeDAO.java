package com.woosan.hr_system.employee.dao;

import com.woosan.hr_system.employee.model.Employee;
import java.util.List;
import java.util.Map;

public interface EmployeeDAO {
    List<Employee> getAllEmployees();
    Employee getEmployeeById(String employeeId);
    Employee getEmployeeDetails(String employeeId);
    String getEmployeeName(String employeeId);
    boolean existsById(String employeeId);
    List<Employee> getEmployeesByDepartment(String departmentId);
    List<Employee> getPreResignationEmployees();
    List<Employee> getResignedEmployees();
    List<Employee> getPreDeletionEmployees();
    Employee getResignedEmployee(String employeeId);
    int countEmployeesByYear(int year);
    Map<String, Integer> selectFamilyInfoById(String employeeId);
    List<Employee> selectEmployeesByDepartmentAndPosition(Map<String, Object> map);
    void insertEmployee(Employee employee);
    void updateEmployee(Employee employee);
    void updateStatus(Map<String, Object> params);
    void updatePosition(Map<String, Object> params);
    void deleteEmployee(String employeeId);
    List<Employee> searchEmployees(String keyword, int pageSize, int offset, String department, String status);
    int countEmployees(String keyword, String department, String status);
}
