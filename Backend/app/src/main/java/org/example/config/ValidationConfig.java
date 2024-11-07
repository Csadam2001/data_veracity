package org.example.config;

import java.util.List;

public class ValidationConfig {
    private Meta meta;
    private List<Objective> objectives;

    public Meta getMeta() {
        return meta;
    }
    public void setMeta(Meta meta) {
        this.meta = meta;
    }
    public List<Objective> getObjectives() {
        return objectives;
    }
    public void setObjectives(List<Objective> objectives) {
        this.objectives = objectives;
    }
}
