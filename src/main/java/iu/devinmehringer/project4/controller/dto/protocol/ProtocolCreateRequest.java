package iu.devinmehringer.project4.controller.dto.protocol;

import java.util.ArrayList;
import java.util.List;

public class ProtocolCreateRequest {

    private Long id;

    private String name;

    private String description;

    private List<ProtocolCreateRequest> steps = new ArrayList<>();

    private List<Integer> dependsOn = new ArrayList<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) {
        this.description = description;
    }

    public List<ProtocolCreateRequest> getSteps() { return steps; }
    public void setSteps(List<ProtocolCreateRequest> steps) {
        this.steps = steps;
    }

    public List<Integer> getDependsOn() { return dependsOn; }
    public void setDependsOn(List<Integer> dependsOn) {
        this.dependsOn = dependsOn;
    }
}