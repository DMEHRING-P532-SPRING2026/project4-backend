package iu.devinmehringer.project4.manager;

import iu.devinmehringer.project4.access.PlanAccess;
import iu.devinmehringer.project4.access.ProtocolAccess;
import iu.devinmehringer.project4.access.ResourceTypeAccess;
import iu.devinmehringer.project4.controller.dto.plan.ActionCreateRequest;
import iu.devinmehringer.project4.controller.dto.plan.PlanCreateRequest;
import iu.devinmehringer.project4.controller.dto.plan.PlanReportResponse;
import iu.devinmehringer.project4.controller.exception.ProtocolNotFoundException;
import iu.devinmehringer.project4.manager.engine.DepthFirstPlanIterator;
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

@Service
@Transactional
public class PlanManager {

    private final PlanAccess planAccess;
    private final ProtocolAccess protocolAccess;
    private final ResourceTypeAccess resourceTypeAccess;

    public PlanManager(
            PlanAccess planAccess,
            ProtocolAccess protocolAccess,
            ResourceTypeAccess resourceTypeAccess) {
        this.planAccess = planAccess;
        this.protocolAccess = protocolAccess;
        this.resourceTypeAccess = resourceTypeAccess;
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

    private void buildFromProtocol(Plan plan, Protocol protocol, PlanCreateRequest request) {
        List<ProtocolStep> steps = protocol.getProtocolSteps()
                .stream()
                .filter(Objects::nonNull)
                .toList();

        List<PlanNode> nodes = new ArrayList<>();
        for (ProtocolStep step : steps) {
            Protocol subProtocol = step.getReferencedProtocol();
            PlanNode node = createNode(plan, subProtocol, request);
            nodes.add(node);
            plan.addChild(node);
        }
    }

    private PlanNode createNode(Plan parent, Protocol protocol, PlanCreateRequest request) {
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

    public DepthFirstPlanIterator getIterator(Plan plan) {
        return new DepthFirstPlanIterator(plan);
    }

    public List<PlanReportResponse> generateReport(Long id) {
        Plan plan = planAccess.getPlan(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Plan not found: " + id));

        List<ResourceType> resourceTypes = resourceTypeAccess.getResourceTypes();
        List<PlanReportResponse> report = new ArrayList<>();

        DepthFirstPlanIterator iterator = new DepthFirstPlanIterator(plan);
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
}