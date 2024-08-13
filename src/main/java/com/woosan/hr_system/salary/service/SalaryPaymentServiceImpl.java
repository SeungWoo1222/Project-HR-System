package com.woosan.hr_system.salary.service;

import com.woosan.hr_system.salary.dao.SalaryPaymentDAO;
import com.woosan.hr_system.salary.model.SalaryPayment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class SalaryPaymentServiceImpl implements SalaryPaymentService {
    @Autowired
    private SalaryPaymentDAO salaryPaymentDAO;

    @Override
    public void addSalaryPayment(SalaryPayment salaryPayment) {
        salaryPaymentDAO.insertSalaryPayment(salaryPayment);
    }

    @Override
    public SalaryPayment getSalaryPaymentById(int paymentId) {
        return salaryPaymentDAO.selectSalaryPaymentById(paymentId);
    }

    @Override
    public List<SalaryPayment> getAllSalaryPayments() {
        return salaryPaymentDAO.selectAllSalaryPayments();
    }

    @Override
    public void updateSalaryPayment(SalaryPayment salaryPayment) {
        salaryPaymentDAO.updateSalaryPayment(salaryPayment);
    }

    @Override
    public void removeSalaryPayment(int paymentId) {
        salaryPaymentDAO.deleteSalaryPayment(paymentId);
    }
}
