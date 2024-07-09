package com.woosan.hr_system.employee.model;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@MappedTypes(Position.class)
public class PositionTypeHandler implements TypeHandler<Position> {

    @Override
    public void setParameter(PreparedStatement ps, int i, Position parameter, JdbcType jdbcType) throws SQLException {
        if (parameter == null) {
            ps.setNull(i, JdbcType.INTEGER.TYPE_CODE);
        } else {
            ps.setInt(i, parameter.getRank());
        }
    }

    @Override
    public Position getResult(ResultSet rs, String columnName) throws SQLException {
        int rank = rs.getInt(columnName);
        if (rs.wasNull()) {
            return null;
        }
        return Position.fromRank(rank);
    }

    @Override
    public Position getResult(ResultSet rs, int columnIndex) throws SQLException {
        int rank = rs.getInt(columnIndex);
        if (rs.wasNull()) {
            return null;
        }
        return Position.fromRank(rank);
    }

    @Override
    public Position getResult(CallableStatement cs, int columnIndex) throws SQLException {
        int rank = cs.getInt(columnIndex);
        if (cs.wasNull()) {
            return null;
        }
        return Position.fromRank(rank);
    }
}
