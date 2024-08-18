package com.woosan.hr_system.salary.service;

import com.woosan.hr_system.salary.model.Salary;
import java.util.List;

public interface SalaryService {
    Salary getSalaryById(int salaryId);
    Salary getSalaryByEmployeeId(String employeeId);
    List<Integer> getSalaryIdList(String employeeId);
    List<Salary> getAllSalaries();
    String addSalary(Salary salary, String employeeId);
    String updateSalary(Salary salary, String employeeId);
    String removeSalary(int salaryId);
}
