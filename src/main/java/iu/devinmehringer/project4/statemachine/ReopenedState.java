package iu.devinmehringer.project4.statemachine;

import iu.devinmehringer.project4.controller.exception.IllegalStateTransitionException;
import iu.devinmehringer.project4.model.plan.ActionStateEnum;
import org.springframework.stereotype.Component;

@Component
public class ReopenedState implements ActionState {

    @Override
    public void implement(ActionContext ctx) {
        throw new IllegalStateTransitionException("REOPENED", "implement");
    }

    @Override
    public void submitForApproval(ActionContext ctx) {
        throw new IllegalStateTransitionException("REOPENED", "submitForApproval");
    }

    @Override
    public void approve(ActionContext ctx) {
        throw new IllegalStateTransitionException("REOPENED", "approve");
    }

    @Override
    public void reject(ActionContext ctx) {
        throw new IllegalStateTransitionException("REOPENED", "reject");
    }

    @Override
    public void reopen(ActionContext ctx) {
        throw new IllegalStateTransitionException("REOPENED", "reopen");
    }

    @Override
    public void suspend(ActionContext ctx, String reason) {
        throw new IllegalStateTransitionException("REOPENED", "suspend");
    }

    @Override
    public void resume(ActionContext ctx) {
        throw new IllegalStateTransitionException("REOPENED", "resume");
    }

    @Override
    public void complete(ActionContext ctx) {
        ctx.getAction().setState(ActionStateEnum.COMPLETED);
    }

    @Override
    public void abandon(ActionContext ctx) {
        ctx.getAction().setState(ActionStateEnum.ABANDONED);
    }

    @Override
    public String name() { return "REOPENED"; }
}