package iu.devinmehringer.project4.model.plan;

public interface PlanNodeVisitor {
    void visitLeaf(ProposedAction leaf);
    void visitComposite(Plan plan);
}