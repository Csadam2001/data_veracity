package org.example;
import java.util.List;

public class ValidationConfig {
    private String datasource;
    private String connection_string;
    private String table_name;
    private List<Expectation> expectations;

    public ValidationConfig(String datasource, String connection_string, String table_name, List<Expectation> expectations) {
        this.datasource = datasource;
        this.connection_string = connection_string;
        this.table_name = table_name;
        this.expectations = expectations;
    }

    // Getters and setters
    public String getDatasource() {
        return datasource;
    }

    public void setDatasource(String datasource) {
        this.datasource = datasource;
    }

    public String getConnection_string() {
        return connection_string;
    }

    public void setConnection_string(String connection_string) {
        this.connection_string = connection_string;
    }

    public String getTable_name() {
        return table_name;
    }

    public void setTable_name(String table_name) {
        this.table_name = table_name;
    }

    public List<Expectation> getExpectations() {
        return expectations;
    }

    public void setExpectations(List<Expectation> expectations) {
        this.expectations = expectations;
    }
}