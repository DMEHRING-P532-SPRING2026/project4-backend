package iu.devinmehringer.project4.manager.engine;

import iu.devinmehringer.project4.model.plan.Plan;
import iu.devinmehringer.project4.model.plan.PlanNodeVisitor;
import iu.devinmehringer.project4.model.plan.ProposedAction;
import iu.devinmehringer.project4.model.resource.ResourceAllocation;
import java.math.BigDecimal;
import java.util.List;
import java.util.function.Function;

public class ResourceCostVisitor implements PlanNodeVisitor {

    private BigDecimal totalCost = BigDecimal.ZERO;
    private final Function<Long, List<ResourceAllocation>> allocationLoader;

    public ResourceCostVisitor(
            Function<Long, List<ResourceAllocation>> allocationLoader) {
        this.allocationLoader = allocationLoader;
    }

    @Override
    public void visitLeaf(ProposedAction leaf) {
        List<ResourceAllocation> allocations =
                allocationLoader.apply(leaf.getId());

        for (ResourceAllocation allocation : allocations) {
            BigDecimal unitCost = allocation.getResourceType().getUnitCost();
            if (unitCost != null && allocation.getQuantity() != null) {
                totalCost = totalCost.add(
                        allocation.getQuantity().multiply(unitCost));
            }
        }
    }

    @Override
    public void visitComposite(Plan plan) {
    }

    public BigDecimal getTotalCost() { return totalCost; }
}