package com.woosan.hr_system.salary.service;

import com.woosan.hr_system.salary.model.DeductionDetails;
import com.woosan.hr_system.salary.model.PayrollDetails;
import com.woosan.hr_system.salary.model.SalaryPayment;
import com.woosan.hr_system.search.PageRequest;
import com.woosan.hr_system.search.PageResult;

import java.time.YearMonth;
import java.util.List;
import java.util.Map;

public interface SalaryPaymentService {
    SalaryPayment getPaymentById(int paymentId);
    SalaryPayment getPaymentWithSalaryById(int paymentId);
    List<SalaryPayment> getPaymentsByEmployeeId(String employeeId);
    PageResult<SalaryPayment> searchPayslips(PageRequest pageRequest, String department, YearMonth yearMonth);
    PageResult<SalaryPayment> searchMyPayslips(PageRequest pageRequest, String employeeId);
    List<SalaryPayment> getAllPayments();
    String addPayment(int salaryId);
    String addPayment(List<Integer> salaryIdList, String yearmonthString);
    String updatePayment(SalaryPayment payslip);
    String removePayment(int paymentId);
    Map<Integer, Boolean> hasPaidSalaryThisMonth(YearMonth yearMonth);
    String updatePayrollRatios(PayrollDetails payrollRatios);
    String updateDeductionRatios(DeductionDetails deductionRatios);
}
