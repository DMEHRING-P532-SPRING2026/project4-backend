package iu.devinmehringer.project4.model.resource;

import iu.devinmehringer.project4.controller.exception.IllegalStateTransitionException;
import iu.devinmehringer.project4.model.plan.ActionStateEnum;
import iu.devinmehringer.project4.model.plan.Suspension;
import iu.devinmehringer.project4.statemachine.ActionContext;
import iu.devinmehringer.project4.statemachine.ActionState;
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
        ctx.getAction().getSuspensions().stream()
                .filter(Suspension::isActive)
                .forEach(s -> s.setEndDate(LocalDate.now()));
        ctx.getAction().setState(ActionStateEnum.IN_PROGRESS);
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
}