package com.woosan.hr_system.salary.service;

import com.woosan.hr_system.salary.model.SalaryPayment;

import java.util.Map;

public interface SalaryCalculation {
    String calculateSalaryPayment(String employeeId, int annualSalary, Map<String, Integer> components);
    SalaryPayment calculateDeductions(SalaryPayment salaryPayment, String employeeId);
}
