package iu.devinmehringer.project4.model.resource;


import iu.devinmehringer.project4.controller.exception.IllegalStateTransitionException;
import iu.devinmehringer.project4.model.plan.ActionStateEnum;
import iu.devinmehringer.project4.model.plan.Suspension;
import iu.devinmehringer.project4.statemachine.ActionContext;
import iu.devinmehringer.project4.statemachine.ActionState;
import org.springframework.stereotype.Component;
import java.time.LocalDate;

@Component
public class InProgressState implements ActionState {

    @Override
    public void implement(ActionContext ctx) {
        throw new IllegalStateTransitionException("IN_PROGRESS", "implement");
    }

    @Override
    public void suspend(ActionContext ctx, String reason) {
        Suspension suspension = new Suspension();
        suspension.setProposedAction(ctx.getAction());
        suspension.setReason(reason);
        suspension.setStartDate(LocalDate.now());
        ctx.getAction().getSuspensions().add(suspension);
        ctx.getAction().setState(ActionStateEnum.SUSPENDED);
    }

    @Override
    public void resume(ActionContext ctx) {
        throw new IllegalStateTransitionException("IN_PROGRESS", "resume");
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
    public String name() { return "IN_PROGRESS"; }
}