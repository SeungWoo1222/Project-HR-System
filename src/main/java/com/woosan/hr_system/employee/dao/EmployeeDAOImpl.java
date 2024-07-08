package com.woosan.hr_system.employee.dao;

import com.woosan.hr_system.employee.model.Employee;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class EmployeeDAOImpl implements EmployeeDAO {

    @Autowired
    private SqlSession sqlSession;

    private static final String NAMESPACE = "com.woosan.hr_system.employee.dao.EmployeeDAO";

    @Override
    public Employee getEmployeeById(String employeeId) {
        return sqlSession.selectOne(NAMESPACE + ".getEmployeeById", employeeId);
    }

    @Override
    public List<Employee> getAllEmployees() {
        return sqlSession.selectList(NAMESPACE + ".getAllEmployees");
    }

    @Override
    public void insertEmployee(Employee employee) {
        sqlSession.insert(NAMESPACE + ".insertEmployee", employee);
    }

    @Override
    public void updateEmployee(Employee employee) {
        sqlSession.update(NAMESPACE + ".updateEmployee", employee);
    }

    @Override
    public void deleteEmployee(String employeeId) {
        sqlSession.delete(NAMESPACE + ".deleteEmployee", employeeId);
    }
}
