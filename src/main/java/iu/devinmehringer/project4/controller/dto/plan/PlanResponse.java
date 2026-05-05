package iu.devinmehringer.project4.controller.dto.plan;

import iu.devinmehringer.project4.model.plan.ActionStateEnum;
import iu.devinmehringer.project4.model.plan.Plan;
import iu.devinmehringer.project4.model.plan.PlanNode;
import iu.devinmehringer.project4.model.plan.ProposedAction;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PlanResponse {

    private Long id;
    private String name;
    private Long sourceProtocolId;
    private LocalDate targetStartDate;
    private ActionStateEnum status;
    private List<PlanNodeResponse> children = new ArrayList<>();

    public static class PlanNodeResponse {
        private Long id;
        private String name;
        private String type;
        private ActionStateEnum status;
        private List<PlanNodeResponse> children = new ArrayList<>();

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public ActionStateEnum getStatus() { return status; }
        public void setStatus(ActionStateEnum status) { this.status = status; }

        public List<PlanNodeResponse> getChildren() { return children; }
        public void setChildren(List<PlanNodeResponse> children) {
            this.children = children;
        }
    }

    // full tree — no depth limit
    public static PlanResponse from(Plan plan) {
        return from(plan, null);
    }

    // depth-limited tree — stops recursing at maxDepth
    public static PlanResponse from(Plan plan, Integer maxDepth) {
        PlanResponse response = new PlanResponse();
        response.setId(plan.getId());
        response.setName(plan.getName());
        response.setTargetStartDate(plan.getTargetStartDate());
        response.setStatus(plan.getStatus());

        if (plan.getSourceProtocol() != null) {
            response.setSourceProtocolId(plan.getSourceProtocol().getId());
        }

        for (PlanNode child : plan.getChildren()) {
            response.getChildren().add(buildNodeResponse(child, 1, maxDepth));
        }

        return response;
    }

    private static PlanNodeResponse buildNodeResponse(PlanNode node,
                                                      int currentDepth, Integer maxDepth) {
        PlanNodeResponse response = new PlanNodeResponse();
        response.setId(node.getId());
        response.setName(node.getName());
        response.setStatus(node.getStatus());

        if (node instanceof Plan plan) {
            response.setType("PLAN");
            if (maxDepth == null || currentDepth < maxDepth) {
                for (PlanNode child : plan.getChildren()) {
                    response.getChildren().add(
                            buildNodeResponse(child, currentDepth + 1, maxDepth));
                }
            }
        } else if (node instanceof ProposedAction) {
            response.setType("ACTION");
        }

        return response;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Long getSourceProtocolId() { return sourceProtocolId; }
    public void setSourceProtocolId(Long sourceProtocolId) {
        this.sourceProtocolId = sourceProtocolId;
    }

    public LocalDate getTargetStartDate() { return targetStartDate; }
    public void setTargetStartDate(LocalDate targetStartDate) {
        this.targetStartDate = targetStartDate;
    }

    public ActionStateEnum getStatus() { return status; }
    public void setStatus(ActionStateEnum status) { this.status = status; }

    public List<PlanNodeResponse> getChildren() { return children; }
    public void setChildren(List<PlanNodeResponse> children) {
        this.children = children;
    }
}