package org.example.config;

public class Evaluation {
    private String target;
    private String type;
    private Args args;
    private Property schema; 
    private Double min;
    private Double max;

    public Evaluation() {}

    public Evaluation(String target, String type, Args args) {
        this.target = target;
        this.type = type;
        this.args = args;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Property getSchema() {   
        return schema;
    }

    public void setSchema(Property schema) {   
        this.schema = schema;
    }

    public Args getArgs() {
        return args;
    }

    public void setArgs(Args args) {
        this.args = args;
    }
    public Double getMin() {
        return min;
    }

    public void setMin(double min) {
        this.min = min;
    }

    public Double getMax() {
        return max;
    }

    public void setMax(double max) {
        this.max = max;
    }
}
