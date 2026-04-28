package iu.devinmehringer.project4.controller.dto.resource;

import iu.devinmehringer.project4.model.knowledge.ResourceTypeKind;

public class ResourceTypeRequest {

    private String name;
    private ResourceTypeKind kind;
    private String unitOfMeasure;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ResourceTypeKind getKind() {
        return kind;
    }

    public void setKind(ResourceTypeKind kind) {
        this.kind = kind;
    }

    public String getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public void setUnitOfMeasure(String unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }
}