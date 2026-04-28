package iu.devinmehringer.project4.controller.dto.plan;

import iu.devinmehringer.project4.model.plan.ActionStateEnum;
import java.math.BigDecimal;
import java.util.Map;

public class PlanReportResponse {

    private Long id;
    private String name;
    private String type;
    private ActionStateEnum status;
    private Map<String, BigDecimal> allocatedQuantityByResourceType;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public ActionStateEnum getStatus() { return status; }
    public void setStatus(ActionStateEnum status) { this.status = status; }

    public Map<String, BigDecimal> getAllocatedQuantityByResourceType() {
        return allocatedQuantityByResourceType;
    }
    public void setAllocatedQuantityByResourceType(
            Map<String, BigDecimal> allocatedQuantityByResourceType) {
        this.allocatedQuantityByResourceType = allocatedQuantityByResourceType;
    }
}