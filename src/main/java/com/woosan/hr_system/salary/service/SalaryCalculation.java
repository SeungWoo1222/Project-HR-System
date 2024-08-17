package com.woosan.hr_system.salary.service;

import java.util.Map;

public interface SalaryCalculation {
    String calculateSalaryPayment(String employeeId, int annualSalary, Map<String, Integer> components);
}
