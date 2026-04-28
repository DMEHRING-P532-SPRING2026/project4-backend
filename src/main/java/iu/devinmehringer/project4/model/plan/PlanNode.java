package iu.devinmehringer.project4.model.plan;

import iu.devinmehringer.project4.model.knowledge.ResourceType;
import java.math.BigDecimal;

public interface PlanNode {
    Long getId();
    String getName();
    ActionStateEnum getStatus();
    BigDecimal getTotalAllocatedQuantity(ResourceType resourceType);
    void accept(PlanNodeVisitor visitor);
}