package iu.devinmehringer.project4.controller.dto.protocol;

import java.util.List;

public class ProtocolUpdateRequest {
    private String name;
    private String description;
    private List<Long> addIds;
    private List<Long> dependsOn;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Long> getAddIds() {
        return addIds;
    }

    public void setAddIds(List<Long> addIds) {
        this.addIds = addIds;
    }

    public List<Long> getDependsOn() {
        return dependsOn;
    }

    public void setDependsOn(List<Long> dependsOn) {
        this.dependsOn = dependsOn;
    }
}
