package com.woosan.hr_system;

import com.woosan.hr_system.employee.model.Employee;
import com.woosan.hr_system.employee.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class DatabaseConnectionTest {

    @Autowired
    private EmployeeService employeeService;

    // Enum과 MyBatis 매핑 오류 문제로 테스트
    @Test
    void testDatabaseConnection() {
        Employee employee = employeeService.getEmployeeById("QC3333333");
        if (employee != null) {
            System.out.println("Employee found: " + employee.getName());
        } else {
            System.out.println("Employee not found.");
        }
    }
}
