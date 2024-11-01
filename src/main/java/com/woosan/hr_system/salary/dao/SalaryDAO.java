package com.woosan.hr_system.salary.dao;

import com.woosan.hr_system.employee.model.Employee;
import com.woosan.hr_system.salary.model.Salary;
import java.time.YearMonth;
import java.util.List;

public interface SalaryDAO {
    Salary selectSalaryById(int salaryId);
    Salary selectSalaryByEmployeeId(String employeeId);
    List<Salary> selectSalariesByIds(List<Integer> salaryIdList);
    List<Integer> selectSalaryIdList(String employeeId);
    List<Salary> searchSalaries(String keyword, int pageSize, int offset, String department, String status);
    int countSalaries(String keyword, String department, String status);
    List<Salary> searchUsingSalaries(String keyword, int pageSize, int offset, String department, YearMonth yearMonth);
    int countUsingSalaries(String keyword, String department, YearMonth yearMonth);
    List<Integer> selectUsingSalaryIdList();
    List<Salary> selectAllSalaries();
    List<Employee> selectEmployeeList();
    void insertSalary(Salary salary);
    void updateSalary(Salary salary);
    void deleteSalary(int salaryId);
    void updateAccountInfo(Salary accountInfo);
    void deactivateSalary(int salaryId);
}
