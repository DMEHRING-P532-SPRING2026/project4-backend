package iu.devinmehringer.project4.manager;

import iu.devinmehringer.project4.access.PlanAccess;
import iu.devinmehringer.project4.access.ProtocolAccess;
import iu.devinmehringer.project4.access.ResourceAllocationAccess;
import iu.devinmehringer.project4.access.ResourceTypeAccess;
import iu.devinmehringer.project4.controller.dto.plan.*;
import iu.devinmehringer.project4.controller.exception.ProtocolNotFoundException;
import iu.devinmehringer.project4.manager.engine.*;
import iu.devinmehringer.project4.model.knowledge.Protocol;
import iu.devinmehringer.project4.model.knowledge.ProtocolStep;
import iu.devinmehringer.project4.model.knowledge.ResourceType;
import iu.devinmehringer.project4.model.plan.ActionStateEnum;
import iu.devinmehringer.project4.model.plan.Plan;
import iu.devinmehringer.project4.model.plan.PlanNode;
import iu.devinmehringer.project4.model.plan.ProposedAction;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Predicate;

@Service
@Transactional
public class PlanManager {

    private final PlanAccess planAccess;
    private final ProtocolAccess protocolAccess;
    private final ResourceTypeAccess resourceTypeAccess;
    private final ResourceAllocationAccess resourceAllocationAccess;

    public PlanManager(
            PlanAccess planAccess,
            ProtocolAccess protocolAccess,
            ResourceTypeAccess resourceTypeAccess,
            ResourceAllocationAccess resourceAllocationAccess) {
        this.planAccess = planAccess;
        this.protocolAccess = protocolAccess;
        this.resourceTypeAccess = resourceTypeAccess;
        this.resourceAllocationAccess = resourceAllocationAccess;
    }

    public Plan createPlan(PlanCreateRequest request) {
        Plan plan = new Plan();
        plan.setName(request.getName());
        plan.setTargetStartDate(request.getTargetStartDate());

        if (request.getSourceProtocolId() != null) {
            Protocol protocol = protocolAccess.getProtocol(
                            request.getSourceProtocolId())
                    .orElseThrow(() -> new ProtocolNotFoundException(
                            request.getSourceProtocolId().toString()));
            plan.setSourceProtocol(protocol);
            buildFromProtocol(plan, protocol, request);
        }

        return planAccess.save(plan);
    }

    private void buildFromProtocol(Plan plan, Protocol protocol,
                                   PlanCreateRequest request) {
        List<ProtocolStep> steps = protocol.getProtocolSteps()
                .stream()
                .filter(Objects::nonNull)
                .toList();

        for (ProtocolStep step : steps) {
            Protocol subProtocol = step.getReferencedProtocol();
            PlanNode node = createNode(plan, subProtocol, request);
            plan.addChild(node);
        }
    }

    private PlanNode createNode(Plan parent, Protocol protocol,
                                PlanCreateRequest request) {
        List<ProtocolStep> subSteps = protocol.getProtocolSteps()
                .stream()
                .filter(Objects::nonNull)
                .toList();

        if (subSteps.isEmpty()) {
            ProposedAction action = new ProposedAction();
            action.setName(protocol.getName());
            action.setProtocol(protocol);
            action.setState(ActionStateEnum.PROPOSED);
            action.setParent(parent);
            return action;
        } else {
            Plan subPlan = new Plan();
            subPlan.setTargetStartDate(request.getTargetStartDate());
            subPlan.setName(protocol.getName());
            subPlan.setSourceProtocol(protocol);
            subPlan.setParent(parent);
            buildFromProtocol(subPlan, protocol, request);
            return subPlan;
        }
    }

    public List<Plan> getPlans() {
        return planAccess.getPlans();
    }

    public Plan getPlan(Long id) {
        return planAccess.getPlan(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Plan not found: " + id));
    }

    public PlanResponse getPlanResponse(Long id, Integer depth) {
        Plan plan = planAccess.getPlan(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Plan not found: " + id));

        return PlanResponse.from(plan, depth);
    }

    public DepthFirstPlanIterator getIterator(Plan plan) {
        return new DepthFirstPlanIterator(plan);
    }

    public Iterator<PlanNode> getFilteredIterator(Plan plan,
                                                  Predicate<PlanNode> predicate) {
        return new FilteredPlanIterator(
                new DepthFirstPlanIterator(plan), predicate);
    }

    public Iterator<PlanNode> getLazyIterator(PlanNode root, int depthLimit) {
        return new LazySubtreeIterator(root, depthLimit);
    }

    public List<PlanReportResponse> generateReport(Long id,
                                                   String statusFilter) {
        Plan plan = planAccess.getPlan(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Plan not found: " + id));

        List<ResourceType> resourceTypes = resourceTypeAccess.getResourceTypes();
        List<PlanReportResponse> report = new ArrayList<>();

        Iterator<PlanNode> iterator;
        if (statusFilter != null && !statusFilter.isBlank()) {
            ActionStateEnum filterState = ActionStateEnum.valueOf(
                    statusFilter.toUpperCase());
            iterator = getFilteredIterator(plan,
                    node -> node.getStatus() == filterState);
        } else {
            iterator = getIterator(plan);
        }

        while (iterator.hasNext()) {
            PlanNode node = iterator.next();

            PlanReportResponse entry = new PlanReportResponse();
            entry.setId(node.getId());
            entry.setName(node.getName());
            entry.setStatus(node.getStatus());
            entry.setType(node instanceof Plan ? "PLAN" : "ACTION");

            Map<String, BigDecimal> quantities = new LinkedHashMap<>();
            for (ResourceType resourceType : resourceTypes) {
                BigDecimal total = node.getTotalAllocatedQuantity(resourceType);
                if (total.compareTo(BigDecimal.ZERO) > 0) {
                    quantities.put(resourceType.getName(), total);
                }
            }
            entry.setAllocatedQuantityByResourceType(quantities);
            report.add(entry);
        }

        return report;
    }

    public Plan addSubPlan(Long parentId, PlanCreateRequest request) {
        Plan parent = planAccess.getPlan(parentId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Plan not found: " + parentId));

        Plan subPlan = new Plan();
        subPlan.setName(request.getName());
        subPlan.setTargetStartDate(request.getTargetStartDate());
        subPlan.setParent(parent);
        parent.addChild(subPlan);

        return planAccess.save(parent);
    }

    public Plan addAction(Long planId, ActionCreateRequest request) {
        Plan plan = planAccess.getPlan(planId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Plan not found: " + planId));

        ProposedAction action = new ProposedAction();
        action.setName(request.getName());
        action.setParty(request.getParty());
        action.setTimeRef(request.getTimeRef());
        action.setLocation(request.getLocation());
        action.setState(ActionStateEnum.PROPOSED);
        action.setParent(plan);
        plan.addChild(action);

        return planAccess.save(plan);
    }

    private PlanNode findNode(Plan plan, Long nodeId) {
        if (plan.getId().equals(nodeId)) return plan;
        Iterator<PlanNode> iterator = getIterator(plan);
        while (iterator.hasNext()) {
            PlanNode node = iterator.next();
            if (node.getId().equals(nodeId)) return node;
        }
        return null;
    }

    public PlanMetricsResponse getMetrics(Long planId, Long nodeId) {
        Plan plan = planAccess.getPlan(planId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Plan not found: " + planId));

        PlanNode target = findNode(plan, nodeId);
        if (target == null) throw new IllegalArgumentException(
                "Node not found: " + nodeId + " in plan: " + planId);

        CompletionRatioVisitor completionVisitor = new CompletionRatioVisitor();

        ResourceCostVisitor costVisitor = new ResourceCostVisitor(
                resourceAllocationAccess::findByProposedActionId);

        RiskScoreVisitor riskVisitor = new RiskScoreVisitor();

        target.accept(completionVisitor);
        target.accept(costVisitor);
        target.accept(riskVisitor);

        PlanMetricsResponse response = new PlanMetricsResponse();
        response.setNodeId(target.getId());
        response.setNodeName(target.getName());
        response.setNodeType(target instanceof Plan ? "PLAN" : "ACTION");
        response.setTotalLeaves(completionVisitor.getTotalLeaves());
        response.setCompletedLeaves(completionVisitor.getCompletedLeaves());
        response.setCompletionRatio(completionVisitor.getRatio());
        response.setTotalCost(costVisitor.getTotalCost());
        response.setRiskScore(riskVisitor.getScore());

        return response;
    }
}