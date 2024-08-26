package com.woosan.hr_system.salary.service;

import com.woosan.hr_system.salary.model.SalaryPayment;

import java.time.YearMonth;
import java.util.Map;

public interface SalaryCalculation {
    String calculateSalaryPayment(String employeeId, int annualSalary, Map<String, Integer> components, YearMonth yearMonth);
    SalaryPayment calculateDeductions(SalaryPayment salaryPayment);
}
