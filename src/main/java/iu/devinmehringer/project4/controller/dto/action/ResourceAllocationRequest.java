package iu.devinmehringer.project4.controller.dto.action;

import iu.devinmehringer.project4.model.knowledge.ResourceTypeKind;
import iu.devinmehringer.project4.model.resource.ResourceAllocationKind;

import java.math.BigDecimal;
import java.time.Instant;

public class ResourceAllocationRequest {

    private Long resourceTypeId;
    private BigDecimal quantity;
    private ResourceAllocationKind kind;    // GENERAL or SPECIFIC
    private String assetId;                 // only when SPECIFIC
    private Instant timePeriodStart;        // only when SPECIFIC
    private Instant timePeriodEnd;          // only when SPECIFIC

    public Long getResourceTypeId() { return resourceTypeId; }
    public void setResourceTypeId(Long resourceTypeId) {
        this.resourceTypeId = resourceTypeId;
    }

    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }

    public ResourceAllocationKind getKind() { return kind; }
    public void setKind(ResourceAllocationKind kind) { this.kind = kind; }

    public String getAssetId() { return assetId; }
    public void setAssetId(String assetId) { this.assetId = assetId; }

    public Instant getTimePeriodStart() { return timePeriodStart; }
    public void setTimePeriodStart(Instant timePeriodStart) {
        this.timePeriodStart = timePeriodStart;
    }

    public Instant getTimePeriodEnd() { return timePeriodEnd; }
    public void setTimePeriodEnd(Instant timePeriodEnd) {
        this.timePeriodEnd = timePeriodEnd;
    }
}