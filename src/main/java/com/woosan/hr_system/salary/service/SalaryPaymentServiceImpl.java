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
    private SalaryService salaryService;
    @Autowired
    private SalaryPaymentDAO salaryPaymentDAO;

    @Override // 지급 ID를 이용한 특정 사원의 급여 지급 내역 조회
    public SalaryPayment getPaymentById(int paymentId) {
        return salaryPaymentDAO.selectPaymentById(paymentId);
    }

    @Override // 사원 ID를 이용한 특정 사원의 모든 급여 지급 내역 조회
    public List<SalaryPayment> getPaymentsByEmployeeId(String employeeId) {
        List<Integer> salaryIdList = salaryService.getSalaryIdList(employeeId);
        return salaryPaymentDAO.getPaymentsByEmployeeId(salaryIdList);
    }

    @Override // 모든 급여 지급 내역 조회
    public List<SalaryPayment> getAllPayments() {
        return salaryPaymentDAO.selectAllPayments();
    }

    @Override // 급여 지급 내역 등록
    public String addPayment(SalaryPayment salaryPayment) {
        salaryPaymentDAO.insertPayment(salaryPayment);
        return null;
    }

    @Override // 급여 지급 내용 수정
    public String updatePayment(SalaryPayment salaryPayment, int paymentId) {
        salaryPaymentDAO.updatePayment(salaryPayment);
        return null;
    }

    @Override // 급여 지급 내역 삭제
    public String removePayment(int paymentId) {
        salaryPaymentDAO.deletePayment(paymentId);
        return null;
    }
}
