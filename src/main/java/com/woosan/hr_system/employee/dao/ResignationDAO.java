package com.woosan.hr_system.employee.dao;

import com.woosan.hr_system.employee.model.Resignation;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ResignationDAO {

    @Autowired
    private SqlSession sqlSession;

    private static final String NAMESPACE = "com.woosan.hr_system.employee.dao.ResignationDAO.";

    public Resignation getResignedEmployee(String employeeId) { // id를 이용한 퇴사 사원 정보 조회
        return sqlSession.selectOne(NAMESPACE + "getResignedEmployee" ,employeeId);
    }

    public List<Resignation> getAllResignedEmployees() { // 모든 퇴사 사원 정보 조회
        return sqlSession.selectList(NAMESPACE + "getAllResignedEmployees");
    }

    public void insertResignation(Resignation resignation) { // 새로운 퇴사 사원 정보 삽입
        sqlSession.insert(NAMESPACE + "insertResignation", resignation);
    }

    public void updateResignation(Resignation resignation) { // 기존 퇴사 사원 정보 업데이트
        sqlSession.update(NAMESPACE + "updateResignation", resignation);
    }

    public void deleteResignation(String employeeId) { // 퇴사 사원 정보 삭제
        sqlSession.delete(NAMESPACE + "deleteResignation", employeeId);
    }
}
