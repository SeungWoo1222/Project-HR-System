package com.woosan.hr_system.employee.model;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@MappedTypes(Department.class)
public class DepartmentTypeHandler implements TypeHandler<Department> {

    @Override
    public void setParameter(PreparedStatement ps, int i, Department parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.name());
    }

    @Override
    public Department getResult(ResultSet rs, String columnName) throws SQLException {
        String name = rs.getString(columnName);
        return Department.valueOf(name);
    }

    @Override
    public Department getResult(ResultSet rs, int columnIndex) throws SQLException {
        String name = rs.getString(columnIndex);
        return Department.valueOf(name);
    }

    @Override
    public Department getResult(CallableStatement cs, int columnIndex) throws SQLException {
        String name = cs.getString(columnIndex);
        return Department.valueOf(name);
    }
}
