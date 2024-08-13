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

    private static final String NAMESPACE = "com.woosan.hr_system.salary.mapper.SalaryMapper";

    public void insertSalary(Salary salary) {
        sqlSession.insert(NAMESPACE + ".insertSalary", salary);
    }

    public Salary selectSalaryById(int salaryId) {
        return sqlSession.selectOne(NAMESPACE + ".selectSalaryById", salaryId);
    }

    public List<Salary> selectAllSalaries() {
        return sqlSession.selectList(NAMESPACE + ".selectAllSalaries");
    }

    public void updateSalary(Salary salary) {
        sqlSession.update(NAMESPACE + ".updateSalary", salary);
    }

    public void deleteSalary(int salaryId) {
        sqlSession.delete(NAMESPACE + ".deleteSalary", salaryId);
    }
}
