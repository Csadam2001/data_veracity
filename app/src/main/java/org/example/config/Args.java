package org.example.config;

public class Args {
    private Integer measurement;
    private String unit;

    public Args(Integer measurement, String unit){
        this.measurement = measurement;
        this.unit = unit;
    }

    public Integer getMeasurement() {
        return measurement;
    }

    public void setMeasurement(Integer measurement) {
        this.measurement = measurement;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    
}
