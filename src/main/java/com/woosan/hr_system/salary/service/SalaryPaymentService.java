package com.woosan.hr_system.salary.service;

import com.woosan.hr_system.salary.model.SalaryPayment;
import java.util.List;

public interface SalaryPaymentService {
    void addSalaryPayment(SalaryPayment salaryPayment);
    SalaryPayment getSalaryPaymentById(int paymentId);
    List<SalaryPayment> getAllSalaryPayments();
    void updateSalaryPayment(SalaryPayment salaryPayment);
    void removeSalaryPayment(int paymentId);
}
