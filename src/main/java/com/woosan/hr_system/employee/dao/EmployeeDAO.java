package com.woosan.hr_system.employee.dao;

import com.woosan.hr_system.employee.model.Employee;
import com.woosan.hr_system.search.SearchService;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class EmployeeDAO implements SearchService<Employee> {

    @Autowired
    private SqlSession sqlSession;

    private static final String NAMESPACE = "com.woosan.hr_system.employee.dao.EmployeeDAO.";

    // 모든 사원 정보 조회
    public List<Employee> getAllEmployees() { // 모든 사원 정보 조회
        return sqlSession.selectList(NAMESPACE + "getAllEmployees");
    }

    // 사원 정보 조회
    public Employee getEmployeeById(String employeeId) { return sqlSession.selectOne(NAMESPACE + "getEmployeeById", employeeId); }

    // 사원 정보 등록 - 입사 처리
    public void insertEmployee(Employee employee) { // 사원 정보 등록
        sqlSession.insert(NAMESPACE + "insertEmployee", employee);
    }

    // 사원 정보 수정
    public void updateEmployee(Employee employee) { // 사원 정보 수정
        sqlSession.update(NAMESPACE + "updateEmployee", employee);
    }

    // 사원 정보 삭제 - 퇴사 후 12개월 지난 사원 정보
    public void deleteEmployee(String employeeId) { // 사원 정보 삭제
        sqlSession.delete(NAMESPACE + "deleteEmployee", employeeId);
    }

    // 사원 번호 중복 조회
    public boolean existsById(String employeeId) { // 사원 번호 중복 조회
        return sqlSession.selectOne(NAMESPACE + "existsById", employeeId);
    }

    // 부서를 이용한 특정 사원 정보 조회
    public List<Employee> getEmployeesByDepartment(String departmentId) { return sqlSession.selectList(NAMESPACE + "getEmployeesByDepartment", departmentId); };

    // 퇴사 예정인 사원 정보 조회
    public List<Employee> getPreResignationEmployees() { return sqlSession.selectList(NAMESPACE + "getPreResignationEmployees"); };

    // 퇴사 사원 정보 조회
    public List<Employee> getResignedEmployees() { // 퇴사 후 2개월 이내의 사원 정보 조회
        return sqlSession.selectList(NAMESPACE + "getResignedEmployees");
    };

    // 퇴사 후 12개월이 지난 사원 정보 조회
    public List<Employee> getPreDeletionEmployees() { return sqlSession.selectList(NAMESPACE + "getPreDeletionEmployees"); };

    // 이번 년도 입사한 사람의 수 조회
    public int countEmployeesByCurrentYear() { return sqlSession.selectOne(NAMESPACE + "countEmployeesByCurrentYear"); };

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

    // 사원 재직 상태 수정
    public void updateStatus(Map<String, Object> params) { // 사원 정보 수정
        sqlSession.update(NAMESPACE + "updateStatus", params);
    }
}
