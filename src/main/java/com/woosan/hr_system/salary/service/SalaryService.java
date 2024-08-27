package com.woosan.hr_system.salary.service;

import com.woosan.hr_system.employee.model.Employee;
import com.woosan.hr_system.salary.model.Salary;
import com.woosan.hr_system.search.PageRequest;
import com.woosan.hr_system.search.PageResult;

import java.time.YearMonth;
import java.util.List;

public interface SalaryService {
    Salary getSalaryById(int salaryId);
    Salary getSalaryByEmployeeId(String employeeId);
    List<Integer> getSalaryIdList(String employeeId);
    List<Employee> getEmployeeList();
    PageResult<Salary> searchSalaries(PageRequest pageRequest, String department);
    PageResult<Salary> searchUsingSalaries(PageRequest pageRequest, String department, YearMonth yearMonth);
    List<Salary> getAllSalaries();
    List<Salary> getSalariesByIds(List<Integer> salaryIdList);
    List<Salary> fetchSalaryListByIds(String salaryIds);
    String addSalary(Salary salary);
    String updateSalary(Salary salary, String employeeId);
    String removeSalary(int salaryId);
    String updateAccountInfo(int salaryId, String bank, String accountNumber);
}
