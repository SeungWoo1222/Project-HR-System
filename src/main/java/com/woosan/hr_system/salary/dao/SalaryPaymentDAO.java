package com.woosan.hr_system.salary.dao;

import com.woosan.hr_system.salary.model.SalaryPayment;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.YearMonth;
import java.util.List;
import java.util.Map;

@Repository
public class SalaryPaymentDAO {
    @Autowired
    private SqlSession sqlSession;

    private static final String NAMESPACE = "com.woosan.hr_system.salary.dao.SalaryPaymentDAO.";

    // 지급 ID를 이용한 특정 사원의 급여명세서 조회
    public SalaryPayment selectPaymentById(int paymentId) {
        return sqlSession.selectOne(NAMESPACE + "selectPaymentById", paymentId);
    }

    // 지급 ID를 이용한 특정 사원의 급여명세서 (급여정보 포함) 조회
    public SalaryPayment selectPaymentWithSalaryById(int paymentId) {
        return sqlSession.selectOne(NAMESPACE + "selectPaymentWithSalaryById", paymentId);
    }

    // 급여 ID 리스트를 이용한 특정 사원의 모든 급여명세서 조회
    public List<SalaryPayment> getPaymentsByEmployeeId(List<Integer> salaryIdList) {
        return sqlSession.selectList(NAMESPACE + "selectPaymentsBySalaryIdList", salaryIdList);
    }

    // 모든 급여명세서 조회
    public List<SalaryPayment> selectAllPayments() {
        return sqlSession.selectList(NAMESPACE + "selectAllPayments");
    }

    // 해당 달 모든 급여명세서 조회
    public List<Integer> selectPaymentByMonth(YearMonth yearMonth) {
        String yearMonthString = yearMonth.toString();
        return sqlSession.selectList(NAMESPACE + "selectPaymentByMonth", yearMonthString);
    }

    // 모든 급여명세서 - 검색어에 해당하는 데이터 결과 조회
    public List<SalaryPayment> searchPayslips(Map<String, Object> params) {
        return sqlSession.selectList(NAMESPACE + "searchPayslips", params);
    }

    // 모든 급여명세서 - 검색어에 해당하는 전체 데이터 개수 조회
    public int countPayslips(Map<String, Object> params) {
        return sqlSession.selectOne(NAMESPACE + "countPayslips", params);
    }

    // 급여명세서 등록
    public void insertPayment(SalaryPayment salaryPayment) {
        sqlSession.insert(NAMESPACE + "insertPayment", salaryPayment);
    }
    // 급여명세서 리스트 등록
    public void insertPaymentList(List<SalaryPayment> salaryPaymentList) {
        sqlSession.insert(NAMESPACE + "insertPaymentList", salaryPaymentList);
    }

    // 급여명세서 수정
    public void updatePayment(SalaryPayment salaryPayment) {
        sqlSession.update(NAMESPACE + "updatePayment", salaryPayment);
    }

    // 급여명세서 삭제
    public void deletePayment(int paymentId) {
        sqlSession.delete(NAMESPACE + "deletePayment", paymentId);
    }

    // 내 급여명세서 개수 조회
    public int countMyPayslips() {
        return sqlSession.selectOne(NAMESPACE + "countMyPayslips");
    }
}
