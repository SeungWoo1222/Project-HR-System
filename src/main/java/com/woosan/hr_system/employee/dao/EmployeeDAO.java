package com.woosan.hr_system.employee.dao;

import com.woosan.hr_system.search.SearchService;
import com.woosan.hr_system.employee.model.Employee;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

@Repository
public class EmployeeDAO implements SearchService<Employee> {

    @Autowired
    private SqlSession sqlSession;

    private static final String NAMESPACE = "com.woosan.hr_system.employee.dao.EmployeeDAO.";

    // 모든 사원 정보 조회
    public List<Employee> getAllEmployees() { // 모든 사원 정보 조회
        return sqlSession.selectList(NAMESPACE + "getAllEmployees");
    }

    @Override // 검색과 페이징 로직
    public List<Employee> search(String keyword, int pageSize, int offset) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("keyword", keyword);;
        params.put("pageSize", pageSize);
        params.put("offset", offset);
        return sqlSession.selectList(NAMESPACE + "search", params);
    }

    @Override // 검색어에 해당하는 전체 데이터의 개수 세는 로직
    public int count(String keyword) {
        return sqlSession.selectOne(NAMESPACE + "count", keyword);
    }

    public Employee getEmployeeById(String employeeId) { //
        return sqlSession.selectOne(NAMESPACE + "getEmployeeById", employeeId);
    }

    public List<Employee> getPreResignationEmployees() { // 퇴사 예정인 사원 정보 조회
        return sqlSession.selectList(NAMESPACE + "getPreResignationEmployees");
    };

    public List<Employee> getResignedEmployees() { // 퇴사 후 2개월 이내의 사원 정보 조회
        return sqlSession.selectList(NAMESPACE + "getResignedEmployees");
    };

    public List<Employee> getPreDeletionEmployees() { // 퇴사 후 12개월이 지난 사원 정보 조회
        return sqlSession.selectList(NAMESPACE + "getPreDeletionEmployees");
    };

    public void insertEmployee(Employee employee) { // 사원 정보 등록
        sqlSession.insert(NAMESPACE + "insertEmployee", employee);
    }

    public void updateEmployee(Employee employee) { // 사원 정보 수정
        sqlSession.update(NAMESPACE + "updateEmployee", employee);
    }

    public int countEmployeesByCurrentYear() { // 이번 년도 입사한 사람의 수 조회
        return sqlSession.selectOne(NAMESPACE + "countEmployeesByCurrentYear");
    };

    public void deleteEmployee(String employeeId) { // 사원 정보 삭제
        sqlSession.delete(NAMESPACE + "deleteEmployee", employeeId);
    }

    public int getPasswordCount(String employeeId) {  // 비밀번호 카운트 조회
        return sqlSession.selectOne(NAMESPACE + "employeeId", employeeId);
    }

    public void addPasswordCount(String employeeId) { // 비밀번호 +1 수정
        sqlSession.update(NAMESPACE + "addPasswordCount", employeeId);
    }

    public void removePasswordCount(String employeeId) { // 비밀번호 0으로 수정
        sqlSession.update(NAMESPACE + "removePasswordCount", employeeId);
    }

}
