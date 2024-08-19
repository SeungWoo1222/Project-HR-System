package com.woosan.hr_system.salary.service;

import com.woosan.hr_system.salary.model.SalaryPayment;

import java.util.List;

public interface SalaryPaymentService {
    SalaryPayment getPaymentById(int paymentId);
    List<SalaryPayment> getAllPayments();
    List<SalaryPayment> getPaymentsByEmployeeId(String employeeId);
    String addPayment(int salaryId);
    String updatePayment(SalaryPayment salaryPayment, int paymentId);
    String removePayment(int paymentId);
}
