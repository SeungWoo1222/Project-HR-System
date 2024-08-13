package com.woosan.hr_system.salary.dao;

import com.woosan.hr_system.salary.model.SalaryPayment;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SalaryPaymentDAO {
    @Autowired
    private SqlSession sqlSession;

    private static final String NAMESPACE = "com.woosan.hr_system.salary.mapper.SalaryPaymentMapper";

    public void insertSalaryPayment(SalaryPayment salaryPayment) {
        sqlSession.insert(NAMESPACE + ".insertSalaryPayment", salaryPayment);
    }

    public SalaryPayment selectSalaryPaymentById(int paymentId) {
        return sqlSession.selectOne(NAMESPACE + ".selectSalaryPaymentById", paymentId);
    }

    public List<SalaryPayment> selectAllSalaryPayments() {
        return sqlSession.selectList(NAMESPACE + ".selectAllSalaryPayments");
    }

    public void updateSalaryPayment(SalaryPayment salaryPayment) {
        sqlSession.update(NAMESPACE + ".updateSalaryPayment", salaryPayment);
    }

    public void deleteSalaryPayment(int paymentId) {
        sqlSession.delete(NAMESPACE + ".deleteSalaryPayment", paymentId);
    }
}
