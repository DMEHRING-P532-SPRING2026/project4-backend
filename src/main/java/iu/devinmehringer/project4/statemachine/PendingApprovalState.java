package iu.devinmehringer.project4.statemachine;

import iu.devinmehringer.project4.controller.exception.IllegalStateTransitionException;
import iu.devinmehringer.project4.model.plan.ActionStateEnum;
import org.springframework.stereotype.Component;

@Component
public class PendingApprovalState implements ActionState {

    @Override
    public void implement(ActionContext ctx) {
        throw new IllegalStateTransitionException("PENDING_APPROVAL", "implement");
    }

    @Override
    public void submitForApproval(ActionContext ctx) {
        throw new IllegalStateTransitionException("PENDING_APPROVAL", "submitForApproval");
    }

    @Override
    public void approve(ActionContext ctx) {
        ctx.getAction().setState(ActionStateEnum.IN_PROGRESS);
    }

    @Override
    public void reject(ActionContext ctx) {
        ctx.getAction().setState(ActionStateEnum.PROPOSED);
    }

    @Override
    public void reopen(ActionContext ctx) {
        throw new IllegalStateTransitionException("PENDING_APPROVAL", "reopen");
    }

    @Override
    public void suspend(ActionContext ctx, String reason) {
        throw new IllegalStateTransitionException("PENDING_APPROVAL", "suspend");
    }

    @Override
    public void resume(ActionContext ctx) {
        throw new IllegalStateTransitionException("PENDING_APPROVAL", "resume");
    }

    @Override
    public void complete(ActionContext ctx) {
        throw new IllegalStateTransitionException("PENDING_APPROVAL", "complete");
    }

    @Override
    public void abandon(ActionContext ctx) {
        throw new IllegalStateTransitionException("PENDING_APPROVAL", "abandon");
    }

    @Override
    public String name() { return "PENDING_APPROVAL"; }
}