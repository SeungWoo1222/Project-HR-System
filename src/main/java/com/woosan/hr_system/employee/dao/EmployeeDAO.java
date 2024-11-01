package com.woosan.hr_system.employee.dao;

import com.woosan.hr_system.employee.model.Employee;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
public class EmployeeDAO {
    @Autowired
    private SqlSession sqlSession;

    private static final String NAMESPACE = "com.woosan.hr_system.employee.dao.EmployeeDAO.";

    // 모든 사원 정보 조회
    public List<Employee> getAllEmployees() { // 모든 사원 정보 조회
        return sqlSession.selectList(NAMESPACE + "getAllEmployees");
    }

    // id를 이용한 특정 사원 정보 조회
    public Employee getEmployeeById(String employeeId) { return sqlSession.selectOne(NAMESPACE + "getEmployeeById", employeeId); }

    // id를 이용한 특정 사원의 모든 정보 조회 (비밀번호 정보, 급여 정보, 퇴사 정보 포함)
    public Employee getEmployeeDetails(String employeeId) {
        return sqlSession.selectOne(NAMESPACE + "selectEmployeeDetails", employeeId);
    }

    // id를 이용한 특정 사원의 이름 조회
    public String getEmployeeName(String employeeId) { return sqlSession.selectOne(NAMESPACE + "getEmployeeName", employeeId); }

    // 사원 번호 중복 조회
    public boolean existsById(String employeeId) { return sqlSession.selectOne(NAMESPACE + "existsById", employeeId); }

    // 부서를 이용한 특정 사원 정보 조회
    public List<Employee> getEmployeesByDepartment(String departmentId) { return sqlSession.selectList(NAMESPACE + "getEmployeesByDepartment", departmentId); };

    // 퇴사 예정인 사원 정보 조회
    public List<Employee> getPreResignationEmployees() { return sqlSession.selectList(NAMESPACE + "getPreResignationEmployees"); };

    // 퇴사 후 2개월 이내의 사원 정보 조회
    public List<Employee> getResignedEmployees() { return sqlSession.selectList(NAMESPACE + "getResignedEmployees"); };

    // 퇴사 후 12개월이 지난 사원 정보 조회
    public List<Employee> getPreDeletionEmployees() { return sqlSession.selectList(NAMESPACE + "getPreDeletionEmployees"); };

    // 퇴사 사원 정보 조회
    public Employee getResignedEmployee(String employeeId) {
        return sqlSession.selectOne(NAMESPACE + "getResignedEmployee", employeeId);
    }

    // 이번 년도 입사한 사람의 수 조회
    public int countEmployeesByYear(int year) { return sqlSession.selectOne(NAMESPACE + "countEmployeesByYear", year); };

    // 사원 가족 정보 조회
    public Map<String, Integer> selectFamilyInfoById(String employeeId) { return sqlSession.selectOne(NAMESPACE + "selectFamilyInfoById", employeeId);}

    // 부서와 직급을 이용한 사원 조회
    public List<Employee> selectEmployeesByDepartmentAndPosition(Map<String, Object> map) {
        return sqlSession.selectList(NAMESPACE + "selectEmployeesByDepartmentAndPosition", map);
    }

    // 사원 정보 등록
    public void insertEmployee(Employee employee) { sqlSession.insert(NAMESPACE + "insertEmployee", employee); }

    // 사원 정보 수정
    public void updateEmployee(Employee employee) { // 사원 정보 수정
        sqlSession.update(NAMESPACE + "updateEmployee", employee);
    }

    // 사원 재직 상태 수정
    public void updateStatus(Map<String, Object> params) { sqlSession.update(NAMESPACE + "updateStatus", params); }

    // 사원 직급 +1으로 수정 - 승진
    public void updatePosition(Map<String, Object> params) { sqlSession.update(NAMESPACE + "updatePosition",params); }

    // 사원 정보 삭제 - 퇴사 후 12개월 지난 사원 정보
    public void deleteEmployee(String employeeId) { sqlSession.delete(NAMESPACE + "deleteEmployee", employeeId); }

    // 모든 사원 정보 - 검색어와 부서에 해당하는 데이터 결과 조회
    public List<Employee> searchEmployees(String keyword, int pageSize, int offset, String department, String status) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("keyword", keyword);
        params.put("pageSize", pageSize);
        params.put("offset", offset);
        params.put("department", department);
        params.put("status", status);
        return sqlSession.selectList(NAMESPACE + "searchEmployees", params);
    }

    // 모든 사원 정보 - 검색어와 부서에 해당하는 전체 데이터 개수 조회
    public int countEmployees(String keyword, String department, String status) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("keyword", keyword);
        params.put("department", department);
        params.put("status", status);
        return sqlSession.selectOne(NAMESPACE + "countEmployees", params);
    }
}
