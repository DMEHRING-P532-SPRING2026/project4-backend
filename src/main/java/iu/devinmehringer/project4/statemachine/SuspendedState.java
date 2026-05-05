package iu.devinmehringer.project4.statemachine;

import iu.devinmehringer.project4.controller.exception.IllegalStateTransitionException;
import iu.devinmehringer.project4.model.plan.ActionStateEnum;
import iu.devinmehringer.project4.model.plan.Suspension;
import org.springframework.stereotype.Component;
import java.time.LocalDate;

@Component
public class SuspendedState implements ActionState {

    @Override
    public void implement(ActionContext ctx) {
        throw new IllegalStateTransitionException("SUSPENDED", "implement");
    }

    @Override
    public void suspend(ActionContext ctx, String reason) {
        throw new IllegalStateTransitionException("SUSPENDED", "suspend");
    }

    @Override
    public void resume(ActionContext ctx) {
        Suspension active = ctx.getAction().getSuspensions().stream()
                .filter(Suspension::isActive)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "No active suspension found"));

        active.setEndDate(LocalDate.now());

        ActionStateEnum previousState = active.getPreviousState();
        ctx.getAction().setState(
                previousState != null ? previousState : ActionStateEnum.PROPOSED);
    }

    @Override
    public void complete(ActionContext ctx) {
        throw new IllegalStateTransitionException("SUSPENDED", "complete");
    }

    @Override
    public void abandon(ActionContext ctx) {
        ctx.getAction().setState(ActionStateEnum.ABANDONED);
    }

    @Override
    public String name() { return "SUSPENDED"; }

    @Override
    public void submitForApproval(ActionContext ctx) {
        throw new IllegalStateTransitionException(name(), "submitForApproval");
    }

    @Override
    public void approve(ActionContext ctx) {
        throw new IllegalStateTransitionException(name(), "approve");
    }

    @Override
    public void reject(ActionContext ctx) {
        throw new IllegalStateTransitionException(name(), "reject");
    }

    @Override
    public void reopen(ActionContext ctx) {
        throw new IllegalStateTransitionException(name(), "reopen");
    }
}