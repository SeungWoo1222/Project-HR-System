package com.woosan.hr_system.salary.dao;

import com.woosan.hr_system.salary.model.DeductionDetails;
import com.woosan.hr_system.salary.model.PayrollDetails;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public class RatioDAO {
    @Autowired
    private SqlSession sqlSession;

    private static final String NAMESPACE = "com.woosan.hr_system.salary.dao.RatioDAO.";

    // 급여 비율 조회
    public PayrollDetails selectPayrollRatios() {
        return sqlSession.selectOne(NAMESPACE + "selectPayrollRatios");
    }

    // 급여 비율 수정
    public void updatePayrollRatio(Map<String, Object> map) {
        sqlSession.update(NAMESPACE + "updatePayrollRatios", map);
    }

    // 공제 비율 조회
    public DeductionDetails selectDeductionRatios() {
        return sqlSession.selectOne(NAMESPACE + "selectDeductionRatios");
    }

    // 공제 비율 수정
    public void updateDeductionRatio(Map<String, Object> map) {
        sqlSession.update(NAMESPACE + "updateDeductionRatios", map);
    }

    // 근로소득세 조회
    public int selectIncomeTax(Map<String, Object> map) {
        return sqlSession.selectOne(NAMESPACE + "selectIncomeTax", map);
    }
}
