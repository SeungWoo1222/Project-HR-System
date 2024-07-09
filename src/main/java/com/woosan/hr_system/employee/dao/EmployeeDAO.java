package com.woosan.hr_system.employee.dao;

import com.woosan.hr_system.employee.model.Employee;
import java.util.List;
public interface EmployeeDAO {

    Employee getEmployeeById(String employeeId); // 특정 사원 정보 조회

    List<Employee> getAllEmployees(); // 모든 사원 정보 조회

    void insertEmployee(Employee employee); // 사원 정보 등록

    void updateEmployee(Employee employee); // 사원 정보 수정

    void deleteEmployee(String employeeId); // 사원 정보 삭제
}
