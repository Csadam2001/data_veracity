package org.example.config;

public class Objective {
    private String description;
    private String aspect;
    private Evaluation evaluation;

    public Objective(String description, String aspect) {
        this.description = description;
        this.aspect = aspect;
    }

    public Evaluation getEvaluation() {
        return evaluation;
    }

    public void setEvaluation(Evaluation evaluation) {
        this.evaluation = evaluation;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAspect() {
        return aspect;
    }

    public void setAspect(String aspect) {
        this.aspect = aspect;
    }

    

    
}
