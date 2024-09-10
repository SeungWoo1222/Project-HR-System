package com.woosan.hr_system.salary.dao;

import com.woosan.hr_system.employee.model.Employee;
import com.woosan.hr_system.salary.model.Salary;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Repository
public class SalaryDAO {
    @Autowired
    private SqlSession sqlSession;

    private static final String NAMESPACE = "com.woosan.hr_system.salary.dao.SalaryDAO.";

    // 급여 ID를 이용한 특정 사원의 급여 정보 조회
    public Salary selectSalaryById(int salaryId) {
        return sqlSession.selectOne(NAMESPACE + "selectSalaryById", salaryId);
    }

    // 사원 ID를 이용한 특정 사원의 급여 정보 조회
    public Salary selectSalaryByEmployeeId(String employeeId) {
        return sqlSession.selectOne(NAMESPACE + "selectSalaryByEmployeeId", employeeId);
    }

    // 사원 ID 리스트를 이용한 사원들의 급여 정보 조회
    public List<Salary> selectSalariesByIds(List<Integer> salaryIdList) {
        return sqlSession.selectList(NAMESPACE + "selectSalariesByIds", salaryIdList);
    }

    // 사원 ID를 이용한 특정 사원의 모든 급여 ID 조회
    public List<Integer> selectSalaryIdList(String employeeId) {
        return sqlSession.selectList(NAMESPACE + "selectSalaryIdList", employeeId);
    }

    // 모든 급여정보 - 검색어와 부서에 해당하는 데이터 결과 조회
    public List<Salary> searchSalaries(String keyword, int pageSize, int offset, String department) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("keyword", keyword);
        params.put("pageSize", pageSize);
        params.put("offset", offset);
        params.put("department", department);
        return sqlSession.selectList(NAMESPACE + "searchSalaries", params);
    }

    // 모든 급여정보 - 검색어와 부서에 해당하는 전체 데이터 개수 조회
    public int countSalaries(String keyword, String department) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("keyword", keyword);
        params.put("department", department);
        return sqlSession.selectOne(NAMESPACE + "countSalaries", params);
    }

    // 급여 지급 조회를 위한 현재 사용하는 급여정보 - 검색어와 부서에 해당하는 데이터 결과 조회
    public List<Salary> searchUsingSalaries(String keyword, int pageSize, int offset, String department, YearMonth yearMonth) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("keyword", keyword);
        params.put("pageSize", pageSize);
        params.put("offset", offset);
        params.put("department", department);
        params.put("status", 1);
        params.put("yearMonth", yearMonth);
        return sqlSession.selectList(NAMESPACE + "searchUsingSalaries", params);
    }

    // 급여 지급 조회를 위한 현재 사용하는 급여정보 - 검색어와 부서에 해당하는 전체 데이터 개수 조회
    public int countUsingSalaries(String keyword, String department, YearMonth yearMonth) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("keyword", keyword);
        params.put("department", department);
        params.put("status", 1);
        params.put("yearMonth", yearMonth);
        return sqlSession.selectOne(NAMESPACE + "countUsingSalaries", params);
    }

    // 현재 사용하는 모든 급여 ID 조회
    public List<Integer> selectUsingSalaryIdList() { return sqlSession.selectList(NAMESPACE + "selectUsingSalaryIdList"); }

    // 모든 사원의 급여 정보 조회
    public List<Salary> selectAllSalaries() {
        return sqlSession.selectList(NAMESPACE + "selectAllSalaries");
    }

    // 급여 정보가 없는 사원 리스트 조회
    public List<Employee> selectEmployeeList() { return sqlSession.selectList(NAMESPACE + "selectEmployeeList"); }

    // 사원 급여 정보 등록
    public void insertSalary(Salary salary) { sqlSession.insert(NAMESPACE + "insertSalary", salary); }

    // 사원 급여 정보 수정
    public void updateSalary(Salary salary) {
        sqlSession.update(NAMESPACE + "updateSalary", salary);
    }

    // 사원 급여 정보 삭제
    public void deleteSalary(int salaryId) {
        sqlSession.delete(NAMESPACE + "deleteSalary", salaryId);
    }

    // 계좌 정보 수정
    public void updateAccountInfo(Salary accountInfo) { sqlSession.update(NAMESPACE + "updateAccountInfo", accountInfo); }

    // 사원 급여 정보 사용 중지
    public void deactivateSalary(int salaryId) { sqlSession.update(NAMESPACE + "deactivateSalary", salaryId); }
}
