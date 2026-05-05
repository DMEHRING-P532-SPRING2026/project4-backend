package iu.devinmehringer.project4.manager.engine;

import iu.devinmehringer.project4.model.plan.ActionStateEnum;
import iu.devinmehringer.project4.model.plan.Plan;
import iu.devinmehringer.project4.model.plan.PlanNodeVisitor;
import iu.devinmehringer.project4.model.plan.ProposedAction;

public class CompletionRatioVisitor implements PlanNodeVisitor {

    private int totalLeaves = 0;
    private int completedLeaves = 0;

    @Override
    public void visitLeaf(ProposedAction leaf) {
        totalLeaves++;
        if (leaf.getStatus() == ActionStateEnum.COMPLETED) {
            completedLeaves++;
        }
    }

    @Override
    public void visitComposite(Plan plan) {
    }

    public double getRatio() {
        if (totalLeaves == 0) return 0.0;
        return (double) completedLeaves / totalLeaves;
    }

    public int getTotalLeaves() { return totalLeaves; }
    public int getCompletedLeaves() { return completedLeaves; }
}