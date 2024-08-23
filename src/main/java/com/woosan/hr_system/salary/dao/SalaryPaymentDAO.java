package com.woosan.hr_system.salary.dao;

import com.woosan.hr_system.salary.model.SalaryPayment;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class SalaryPaymentDAO {
    @Autowired
    private SqlSession sqlSession;

    private static final String NAMESPACE = "com.woosan.hr_system.salary.dao.SalaryPaymentDAO.";

    // 지급 ID를 이용한 특정 사원의 급여 지급 내역 조회
    public SalaryPayment selectPaymentById(int paymentId) {
        return sqlSession.selectOne(NAMESPACE + "selectPaymentById", paymentId);
    }

    // 급여 ID 리스트를 이용한 특정 사원의 모든 급여 지급 내역 조회
    public List<SalaryPayment> getPaymentsByEmployeeId(List<Integer> salaryIdList) {
        return sqlSession.selectList(NAMESPACE + "selectPaymentsBySalaryIdList", salaryIdList);
    }

    // 모든 급여 지급 내역 조회
    public List<SalaryPayment> selectAllPayments() {
        return sqlSession.selectList(NAMESPACE + "selectAllPayments");
    }

    // 해당 달 모든 급여 지급 내역 조회
    public List<Integer> selectPaymentByMonth(YearMonth yearMonth) {
        String yearMonthString = yearMonth.toString();
        return sqlSession.selectList(NAMESPACE + "selectPaymentByMonth", yearMonthString);
    }

    // salaryId와 yearMonth를 이용한 급여명세서 리스트 조회
    public List<SalaryPayment> selectPaymentBySalaryAndMonth(List<Integer> salaryIdList, YearMonth yearMonth) {
        return sqlSession.selectList(NAMESPACE + "selectPaymentBySalaryAndMonth",
                Map.of("salaryIdList", salaryIdList, "yearMonth", yearMonth));
    }

    // 모든 급여명세서 정보 검색과 페이징 로직
    public List<SalaryPayment> searchPayslips(String keyword, int pageSize, int offset) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("keyword", keyword);
        params.put("pageSize", pageSize);
        params.put("offset", offset);
        return sqlSession.selectList(NAMESPACE + "searchPayslips", params);
    }

    // 검색어에 해당하는 전체 데이터의 개수 세는 로직
    public int count(String keyword) {
        return sqlSession.selectOne(NAMESPACE + "count", keyword);
    }

    // 급여 지급 내역 등록
    public void insertPayment(SalaryPayment salaryPayment) {
        sqlSession.insert(NAMESPACE + "insertPayment", salaryPayment);
    }
    // 급여 지급 내역 리스트 등록
    public void insertPaymentList(List<SalaryPayment> salaryPaymentList) {
        sqlSession.insert(NAMESPACE + "insertPaymentList", salaryPaymentList);
    }

    // 급여 지급 내용 수정
    public void updatePayment(SalaryPayment salaryPayment) {
        sqlSession.update(NAMESPACE + "updatePayment", salaryPayment);
    }

    // 급여 지급 내역 삭제
    public void deletePayment(int paymentId) {
        sqlSession.delete(NAMESPACE + "deletePayment", paymentId);
    }
}
