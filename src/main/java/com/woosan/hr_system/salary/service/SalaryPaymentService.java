package com.woosan.hr_system.salary.service;

import com.woosan.hr_system.salary.model.SalaryPayment;

import java.time.YearMonth;
import java.util.List;
import java.util.Map;

public interface SalaryPaymentService {
    SalaryPayment getPaymentById(int paymentId);
    List<SalaryPayment> getAllPayments();
    List<SalaryPayment> getPaymentsByEmployeeId(String employeeId);
    List<SalaryPayment> getPaymentBySalaryAndMonth(List<Integer> salaryIdList, String yearMonthString);
    String addPayment(int salaryId);
    String addPayment(List<Integer> salaryIdList, String yearmonthString);
    String updatePayment(SalaryPayment salaryPayment, int paymentId);
    String removePayment(int paymentId);
    Map<Integer, Boolean> hasPaidSalaryThisMonth(YearMonth yearMonth);
}
