package com.woosan.hr_system.employee.dao;

import com.woosan.hr_system.employee.model.Employee;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class EmployeeDAO {

    @Autowired
    private SqlSession sqlSession;

    private static final String NAMESPACE = "com.woosan.hr_system.employee.dao.EmployeeDAO.";

    public Employee getEmployeeById(String employeeId) { // id를 이용한 특정 사원 정보 조회
        return sqlSession.selectOne(NAMESPACE + "getEmployeeById", employeeId);
    }

    public List<Employee> getAllEmployees() { // 모든 사원 정보 조회
        return sqlSession.selectList(NAMESPACE + "getAllEmployees");
    }

    public void insertEmployee(Employee employee) { // 사원 정보 등록
        sqlSession.insert(NAMESPACE + "insertEmployee", employee);
    }

    public void updateEmployee(Employee employee) { // 사원 정보 수정
        sqlSession.update(NAMESPACE + "updateEmployee", employee);
    }

    public void deleteEmployee(String employeeId) {
        sqlSession.delete(NAMESPACE + "deleteEmployee", employeeId);
    }

    public int countEmployeesByCurrentYear() { // 이번 년도 입사한 사람의 수
        return sqlSession.selectOne(NAMESPACE + "countEmployeesByCurrentYear");
    };

    public List<Employee> getPreResignationEmployees() { // 퇴사 예정인 사원 정보 조회
        return sqlSession.selectList(NAMESPACE + "getPreResignationEmployees");
    };

    public List<Employee> getResignedEmployees() { // 퇴사 후 2개월 이내의 사원 정보 조회
        return sqlSession.selectList(NAMESPACE + "getResignedEmployees");
    };

    public List<Employee> getPreDeletionEmployees() { // 퇴사 후 12개월이 지난 사원 정보 조회
        return sqlSession.selectList(NAMESPACE + "getPreDeletionEmployees");
    };
}
