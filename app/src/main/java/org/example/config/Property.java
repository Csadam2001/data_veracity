package org.example.config;

import java.util.HashMap;
import java.util.Map;

public class Property {
    private String type;
    private Map<String, Property> properties;

    public Property(String type) {
        this.type = type;
        if ("object".equals(type)) {
            this.properties = new HashMap<>();
        }
    }

    public Property(String type, Map<String, Property> properties) {
        this.type = type;
        this.properties = properties;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, Property> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Property> properties) {
        this.properties = properties;
    }
}
