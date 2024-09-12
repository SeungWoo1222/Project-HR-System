package com.woosan.hr_system.vacation.dao;

import com.woosan.hr_system.vacation.model.Vacation;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
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

    // 검색 조건에 맞는 휴가 정보 조회
    public List<Vacation> searchVacation(HashMap<String, Object> params) {
        return sqlSession.selectList(NAMESPACE + "searchVacation", params);
    }

    // 해당 사원의 모든 휴가 정보 조회
    public List<Vacation> selectVacationByEmployeeId(HashMap<String, Object> params) {
        return sqlSession.selectList(NAMESPACE + "selectVacationByEmployeeId", params);
    }

    // 해당 부서의 모든 휴가 정보 조회
    public List<Vacation> selectVacationByDepartmentId(HashMap<String, Object> params) {
        return sqlSession.selectList(NAMESPACE + "selectVacationByDepartmentId", params);
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
        // 휴가 승인 처리 시 연차 차감
        if (updatedVacation.getApprovalStatus().equals("승인")) {
            sqlSession.update(NAMESPACE + "updateRemainingLeave", updatedVacation);
        }
    }

    // 휴가 삭제
    public void deleteVacation(int vacationId) {
        sqlSession.delete(NAMESPACE + "deleteVacation", vacationId);
    }
}
