package com.buaa.act.sdp.util;

import org.apache.ibatis.type.*;

import java.sql.*;

/**
 * Created by yang on 2016/10/19.
 */
@MappedTypes({String[].class})
@MappedJdbcTypes({JdbcType.VARCHAR})
public class StringArrayTypeHandler extends BaseTypeHandler<String[]> {

    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, String[] strings, JdbcType jdbcType) throws SQLException {
        if (strings == null)
            preparedStatement.setNull(i, Types.VARCHAR);
        else {
            StringBuffer result = new StringBuffer();
            for (String value : strings) {
                result.append(value).append(",");
            }
            result.deleteCharAt(result.length()-1);
            preparedStatement.setString(i, result.toString());
        }
    }

    @Override
    public String[] getNullableResult(ResultSet resultSet, String s) throws SQLException {
        String columnValue=resultSet.getString(s);
        return getStringArray(columnValue);
    }

    @Override
    public String[] getNullableResult(ResultSet resultSet, int i) throws SQLException {
        String columnValue = resultSet.getString(i);
        return getStringArray(columnValue);
    }

    @Override
    public String[] getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
        String columnValue = callableStatement.getString(i);
        return getStringArray(columnValue);
    }

    private String[] getStringArray(String columnValue) {
        if (columnValue == null)
            return null;
        return columnValue.split(",");
    }
}
