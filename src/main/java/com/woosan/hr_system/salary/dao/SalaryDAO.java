package com.woosan.hr_system.salary.dao;

import com.woosan.hr_system.salary.model.Salary;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SalaryDAO {
    @Autowired
    private SqlSession sqlSession;

    private static final String NAMESPACE = "com.woosan.hr_system.salary.mapper.SalaryMapper.";

    // 사원 급여 정보 등록
    public void insertSalary(Salary salary) {
        sqlSession.insert(NAMESPACE + ".insertSalary", salary);
    }

    // 급여 ID를 이용한 특정 사원의 급여 정보 조회
    public Salary selectSalaryById(int salaryId) {
        return sqlSession.selectOne(NAMESPACE + "selectSalaryById", salaryId);
    }

    // 사원 ID를 이용한 특정 사원의 급여 정보 조회
    public Salary selectSalaryByEmployeeId(String employeeId) {
        return sqlSession.selectOne(NAMESPACE + "selectSalaryByEmployeeId", employeeId);
    }

    // 사원 ID를 이용한 특정 사원의 모든 급여 ID 조회
    public List<Integer> selectSalaryIdList(String employeeId) {
        return sqlSession.selectList(NAMESPACE + "selectSalaryIdList", employeeId);
    }

    // 모든 사원의 급여 정보 조회
    public List<Salary> selectAllSalaries() {
        return sqlSession.selectList(NAMESPACE + "selectAllSalaries");
    }

    // 사원 급여 정보 수정
    public void updateSalary(Salary salary) {
        sqlSession.update(NAMESPACE + "updateSalary", salary);
    }

    // 사원 급여 정보 삭제
    public void deleteSalary(int salaryId) {
        sqlSession.delete(NAMESPACE + "deleteSalary", salaryId);
    }

}
