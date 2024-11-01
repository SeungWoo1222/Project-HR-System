package com.woosan.hr_system.salary.dao;

import com.woosan.hr_system.salary.model.SalaryPayment;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

public interface SalaryPaymentDAO {
    SalaryPayment selectPaymentById(int paymentId);
    SalaryPayment selectPaymentWithSalaryById(int paymentId);
    List<SalaryPayment> getPaymentsByEmployeeId(List<Integer> salaryIdList);
    List<SalaryPayment> selectAllPayments();
    List<Integer> selectPaymentByMonth(YearMonth yearMonth);
    List<SalaryPayment> searchPayslips(Map<String, Object> params);
    int countPayslips(Map<String, Object> params);
    void insertPayment(SalaryPayment salaryPayment);
    void insertPaymentList(List<SalaryPayment> salaryPaymentList);
    void updatePayment(SalaryPayment salaryPayment);
    void deletePayment(int paymentId);
    int countMyPayslips();
}
