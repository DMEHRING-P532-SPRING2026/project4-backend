package iu.devinmehringer.project4.model.plan;

public interface PlanNodeVisitor {
    void visit(Plan plan);
    void visit(ProposedAction action);
}