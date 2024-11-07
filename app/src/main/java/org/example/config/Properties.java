package org.example.config;

import java.util.HashMap;
import java.util.Map;

public class Properties {
    private Map<String, Property> properties = new HashMap<>();

    public void addProperty(String name, Property property) {
        properties.put(name, property);
    }

    public Map<String, Property> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Property> properties) {
        this.properties = properties;
    }
}
