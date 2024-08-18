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

    // 지급 ID를 이용한 특정 사원의 급여 지급 내역 조회
    public SalaryPayment selectPaymentById(int paymentId) {
        return sqlSession.selectOne(NAMESPACE + ".selectPaymentById", paymentId);
    }

    // 급여 ID 리스트를 이용한 특정 사원의 모든 급여 지급 내역 조회
    public List<SalaryPayment> getPaymentsByEmployeeId(List<Integer> salaryIdList) {
        return sqlSession.selectList(NAMESPACE + ".selectPaymentsBySalaryIdList", salaryIdList);
    }

    // 모든 급여 지급 내역 조회
    public List<SalaryPayment> selectAllPayments() {
        return sqlSession.selectList(NAMESPACE + ".selectAllPayments");
    }

    // 급여 지급 내역 등록
    public void insertPayment(SalaryPayment salaryPayment) {
        sqlSession.insert(NAMESPACE + ".insertPayment", salaryPayment);
    }

    // 급여 지급 내용 수정
    public void updatePayment(SalaryPayment salaryPayment) {
        sqlSession.update(NAMESPACE + ".updatePayment", salaryPayment);
    }

    // 급여 지급 내역 삭제
    public void deletePayment(int paymentId) {
        sqlSession.delete(NAMESPACE + ".deletePayment", paymentId);
    }
}
