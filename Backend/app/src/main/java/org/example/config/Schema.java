package org.example.config;

public class Schema {
    private String type;
    private Property property;

    public Schema(String type, Property property) {
        this.type = type;
        this.property = property;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
    }

    
}
