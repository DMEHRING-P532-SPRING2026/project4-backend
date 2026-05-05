package iu.devinmehringer.project4.manager.engine;

import iu.devinmehringer.project4.model.plan.ActionStateEnum;
import iu.devinmehringer.project4.model.plan.Plan;
import iu.devinmehringer.project4.model.plan.PlanNodeVisitor;
import iu.devinmehringer.project4.model.plan.ProposedAction;

public class RiskScoreVisitor implements PlanNodeVisitor {

    private int score = 0;

    @Override
    public void visitLeaf(ProposedAction leaf) {
        if (leaf.getStatus() == ActionStateEnum.SUSPENDED
                || leaf.getStatus() == ActionStateEnum.ABANDONED) {
            score++;
        }
    }

    @Override
    public void visitComposite(Plan plan) {
    }

    public int getScore() { return score; }
}