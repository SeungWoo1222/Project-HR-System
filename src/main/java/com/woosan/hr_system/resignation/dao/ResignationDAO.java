package com.woosan.hr_system.resignation.dao;

import com.woosan.hr_system.resignation.model.Resignation;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ResignationDAO {

    @Autowired
    private SqlSession sqlSession;

    private static final String NAMESPACE = "com.woosan.hr_system.resignation.dao.ResignationDAO.";

    // 모든 퇴사 사원 정보 조회
    public List<Resignation> getAllResignedEmployees() { return sqlSession.selectList(NAMESPACE + "getAllResignedEmployees"); }

    // 퇴사 사원 정보 조회
    public Resignation getResignedEmployee(String employeeId) { return sqlSession.selectOne(NAMESPACE + "getResignedEmployee" ,employeeId); }

    // 퇴사 사원 정보 등록
    public void insertResignation(Resignation resignation) {
        sqlSession.insert(NAMESPACE + "insertResignation", resignation);
        sqlSession.update(NAMESPACE + "processResignation", resignation.getEmployeeId());
    }

    // 퇴사 사원 정보 수정
    public void updateResignation(Resignation resignation) { sqlSession.update(NAMESPACE + "updateResignation", resignation); }

    // 퇴사 사원 정보 삭제 - 퇴사 후 12개월 지난 사원 정보
    public void deleteResignation(String employeeId) { // 퇴사 사원 정보 삭제
        sqlSession.delete(NAMESPACE + "deleteResignation", employeeId);
    }
}
