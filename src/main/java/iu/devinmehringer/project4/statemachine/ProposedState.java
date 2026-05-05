package iu.devinmehringer.project4.statemachine;

import iu.devinmehringer.project4.controller.exception.IllegalStateTransitionException;
import iu.devinmehringer.project4.model.plan.ActionStateEnum;
import iu.devinmehringer.project4.model.plan.Suspension;
import org.springframework.stereotype.Component;
import java.time.LocalDate;

@Component
public class ProposedState implements ActionState {

    @Override
    public void implement(ActionContext ctx) {
        ctx.getAction().setState(ActionStateEnum.IN_PROGRESS);
    }

    @Override
    public void submitForApproval(ActionContext ctx) {
        ctx.getAction().setState(ActionStateEnum.PENDING_APPROVAL);
    }

    @Override
    public void approve(ActionContext ctx) {
        throw new IllegalStateTransitionException("PROPOSED", "approve");
    }

    @Override
    public void reject(ActionContext ctx) {
        throw new IllegalStateTransitionException("PROPOSED", "reject");
    }

    @Override
    public void reopen(ActionContext ctx) {
        throw new IllegalStateTransitionException("PROPOSED", "reopen");
    }

    @Override
    public void suspend(ActionContext ctx, String reason) {
        Suspension suspension = new Suspension();
        suspension.setProposedAction(ctx.getAction());
        suspension.setReason(reason);
        suspension.setStartDate(LocalDate.now());
        suspension.setPreviousState(ActionStateEnum.PROPOSED);
        ctx.getAction().getSuspensions().add(suspension);
        ctx.getAction().setState(ActionStateEnum.SUSPENDED);
    }

    @Override
    public void resume(ActionContext ctx) {
        throw new IllegalStateTransitionException("PROPOSED", "resume");
    }

    @Override
    public void complete(ActionContext ctx) {
        throw new IllegalStateTransitionException("PROPOSED", "complete");
    }

    @Override
    public void abandon(ActionContext ctx) {
        ctx.getAction().setState(ActionStateEnum.ABANDONED);
    }

    @Override
    public String name() { return "PROPOSED"; }
}