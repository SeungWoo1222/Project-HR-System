package com.woosan.hr_system.employee.dao;

import com.woosan.hr_system.employee.model.Employee;
import com.woosan.hr_system.employee.model.Termination;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TerminationDAO {

    @Autowired
    private SqlSession sqlSession;

    private static final String NAMESPACE = "com.woosan.hr_system.employee.dao.TerminationDAO.";

    public Employee getTerminatedEmployee(String employeeId) { // id를 이용한 퇴사 사원 정보 조회
        return sqlSession.selectOne(NAMESPACE + "getTerminatedEmployee" ,employeeId);
    }

    public List<Termination> getAllTerminatedEmployees() { // 모든 퇴사 사원 정보 조회
        return sqlSession.selectList(NAMESPACE + "getAllTerminatedEmployees");
    }

    public void insertTermination(Termination termination) { // 새로운 퇴사 사원 정보 삽입
        sqlSession.insert(NAMESPACE + "insertTermination", termination);
    }

    public void updateTermination(Termination termination) { // 기존 퇴사 사원 정보 업데이트
        sqlSession.update(NAMESPACE + "updateTermination", termination);
    }

    public void deleteTermination(String employeeId) { // 퇴사 사원 정보 삭제
        sqlSession.delete(NAMESPACE + "deleteTermination", employeeId);
    }
}
