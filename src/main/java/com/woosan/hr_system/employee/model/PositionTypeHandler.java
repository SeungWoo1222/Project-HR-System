package com.woosan.hr_system.employee.model;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PositionTypeHandler extends BaseTypeHandler<Position> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Position parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.name());
    }

    @Override
    public Position getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String name = rs.getString(columnName);
        return name != null ? Position.valueOf(name) : null;
    }

    @Override
    public Position getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String name = rs.getString(columnIndex);
        return name != null ? Position.valueOf(name) : null;
    }

    @Override
    public Position getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String name = cs.getString(columnIndex);
        return name != null ? Position.valueOf(name) : null;
    }
}
