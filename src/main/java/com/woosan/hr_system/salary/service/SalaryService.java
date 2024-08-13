package com.woosan.hr_system.salary.service;

import com.woosan.hr_system.salary.model.Salary;
import java.util.List;

public interface SalaryService {
    void addSalary(Salary salary);
    Salary getSalaryById(int salaryId);
    List<Salary> getAllSalaries();
    void updateSalary(Salary salary);
    void removeSalary(int salaryId);
}
