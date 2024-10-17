package com.woosan.hr_system.vacation.dao;

import com.woosan.hr_system.vacation.model.Vacation;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

@Repository
public class VacationDAO {
    @Autowired
    private SqlSession sqlSession;

    // 아이디를 이용한 휴가 정보 조회
    public Vacation selectVacationById(int vacationId) {
        return sqlSession.selectOne("vacation.selectVacationById", vacationId);
    }

    // 휴가 내역 검색 조회
    public List<Vacation> searchVacation(HashMap<String, Object> params) {
        return sqlSession.selectList("vacation.searchVacation", params);
    }

    // 휴가 내역 검색값 개수 조회
    public int countVacation(HashMap<String, Object> params) {
        return sqlSession.selectOne("vacation.countVacation", params);
    }

    // 해당 사원의 모든 휴가 내역 조회
    public List<Vacation> selectVacationsByEmployeeId(HashMap<String, Object> params) {
        return sqlSession.selectList("vacation.selectVacationsByEmployeeId", params);
    }

    // 해당 사원의 모든 휴가 내역 개수 조회
    public int countVacationsByEmployeeId(HashMap<String, Object> params) {
        return sqlSession.selectOne("vacation.countVacationsByEmployeeId", params);
    }

    // 해당 사원의 모든 휴가 정보 조회
    public List<Vacation> getVacationsByEmployeeId(String employeeId) {
        return sqlSession.selectList("vacation.getVacationsByEmployeeId", employeeId);
    }

    // 해당 부서의 모든 휴가 내역 조회
    public List<Vacation> selectVacationsByDepartmentId(HashMap<String, Object> params) {
        return sqlSession.selectList("vacation.selectVacationsByDepartmentId", params);
    }

    // 해당 부서의 모든 휴가 내역 개수 조회
    public int countVacationsByDepartmentId(HashMap<String, Object> params) {
        return sqlSession.selectOne("vacation.countVacationsByDepartmentId", params);
    }

    // 휴가 등록
    public void insertVacation(Vacation vacation) {
        sqlSession.insert("vacation.insertVacation", vacation);
    }

    // 휴가 수정
    public void updateVacation(Vacation vacation) {
        sqlSession.update("vacation.updateVacation", vacation);
    }

    // 휴가 처리
    public void approveVacation(Vacation updatedVacation) {
        sqlSession.update("vacation.approveVacation", updatedVacation);
        // 휴가 승인 처리 시 연차 차감
        if (updatedVacation.getApprovalStatus().equals("승인")) {
            sqlSession.update("vacation.updateRemainingLeave", updatedVacation);
        }
    }

    // 휴가 삭제
    public void deleteVacation(int vacationId) {
        sqlSession.delete("vacation.deleteVacation", vacationId);
    }

    // 오늘 휴가인 사원 조회
    public List<Vacation> getEmployeesOnVacationToday(LocalDate today) {
        return sqlSession.selectList("vacation.getEmployeesOnVacationToday", today);
    }
}
