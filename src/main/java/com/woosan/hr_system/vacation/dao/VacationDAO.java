package com.woosan.hr_system.vacation.dao;

import com.woosan.hr_system.vacation.model.Vacation;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class VacationDAO {
    @Autowired
    private SqlSession sqlSession;

    private static final String NAMESPACE = "com.woosan.hr_system.vacation.dao.VacationDAO.";

    // 아이디를 이용한 휴가 정보 조회
    public Vacation selectVacationById(int vacationId) {
        return sqlSession.selectOne(NAMESPACE + "selectVacationById", vacationId);
    }

    // 해당 사원의 모든 휴가 정보 조회
    public List<Vacation> selectVacationByEmployeeId(String employeeId) {
        return sqlSession.selectList(NAMESPACE + "selectVacationByEmployeeId", employeeId);
    }

    // 해당 부서의 모든 휴가 정보 조회
    public List<Vacation> selectVacationByDepartmentId(List<String> employeeIdList) {
        return sqlSession.selectList(NAMESPACE + "selectVacationByDepartmentId", employeeIdList);
    }

    // 휴가 등록
    public void insertVacation(Vacation vacation) {
        sqlSession.insert(NAMESPACE + "insertVacation", vacation);
    }

    // 휴가 수정
    public void updateVacation(Vacation vacation) {
        sqlSession.update(NAMESPACE + "updateVacation", vacation);
    }

    // 휴가 처리
    public void approveVacation(Vacation updatedVacation) {
        sqlSession.update(NAMESPACE + "approveVacation", updatedVacation);
        sqlSession.update(NAMESPACE + "updateRemainingLeave", updatedVacation);
    }
}
