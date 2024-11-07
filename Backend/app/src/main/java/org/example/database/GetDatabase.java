package org.example.database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
public class GetDatabase {
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/postgres";
    private static final String USER = "postgres";
    private static final String PASS = "asd";


    public static List<String> getTableColumnNames(String tableName) {
        Connection conn = null;
        ResultSet rs = null;
        List<String> columnNames = new ArrayList<>();
        try {
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            DatabaseMetaData dbMetaData = conn.getMetaData();

            rs = dbMetaData.getColumns(null, null, tableName, null);
            while (rs.next()) {
                String columnName = rs.getString("COLUMN_NAME");
                columnNames.add(columnName);
                String columnType = rs.getString("TYPE_NAME");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return columnNames;
    }
    public static List<String> getTableColumnTypes(String tableName) {
        Connection conn = null;
        ResultSet rs = null;
        List<String> columnTypes = new ArrayList<>();
        try {
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            DatabaseMetaData dbMetaData = conn.getMetaData();

            rs = dbMetaData.getColumns(null, null, tableName, null);
            while (rs.next()) {
                String columnType = rs.getString("TYPE_NAME");
                columnTypes.add(columnType);
                
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return columnTypes;
    }
}
