package iu.devinmehringer.project4.controller.dto.resource;

import iu.devinmehringer.project4.model.knowledge.ResourceType;
import iu.devinmehringer.project4.model.knowledge.ResourceTypeKind;

public class ResourceTypeResponse {

    private Long id;
    private String name;
    private ResourceTypeKind kind;
    private String unitOfMeasure;
    private Long poolAccountId;

    public static ResourceTypeResponse from(ResourceType resourceType) {
        ResourceTypeResponse response = new ResourceTypeResponse();
        response.setId(resourceType.getId());
        response.setName(resourceType.getName());
        response.setKind(resourceType.getKind());
        response.setUnitOfMeasure(resourceType.getUnitOfMeasure());
        if (resourceType.getPoolAccount() != null) {
            response.setPoolAccountId(resourceType.getPoolAccount().getId());
        }
        return response;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public ResourceTypeKind getKind() { return kind; }
    public void setKind(ResourceTypeKind kind) { this.kind = kind; }

    public String getUnitOfMeasure() { return unitOfMeasure; }
    public void setUnitOfMeasure(String unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }

    public Long getPoolAccountId() { return poolAccountId; }
    public void setPoolAccountId(Long poolAccountId) {
        this.poolAccountId = poolAccountId;
    }
}