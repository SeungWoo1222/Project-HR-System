package com.woosan.hr_system.salary.dao;

import com.woosan.hr_system.salary.model.DeductionDetails;
import com.woosan.hr_system.salary.model.PayrollDetails;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class RatioDAO {
    @Autowired
    private SqlSession sqlSession;

    private static final String NAMESPACE = "com.woosan.hr_system.salary.dao.RatioDAO.";

    // 급여 비율 조회
    public List<PayrollDetails> selectPayrollRatios() {
        return sqlSession.selectList(NAMESPACE + "selectPayrollRatios");
    }

    // 급여 비율 수정
    public void updatePayrollRatio(Map<String, Object> map) {
        sqlSession.update(NAMESPACE + "updatePayrollRatios", map);
    }

    // 공제 비율 조회
    public List<DeductionDetails> selectDeductionRatios() {
        return sqlSession.selectList(NAMESPACE + "selectDeductionRatios");
    }

    // 공제 비율 수정
    public void updateDeductionRatio(Map<String, Object> map) {
        sqlSession.update(NAMESPACE + "updateDeductionRatios", map);
    }
}
