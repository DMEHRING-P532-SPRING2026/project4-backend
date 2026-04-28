package iu.devinmehringer.project4.model.resource;

import iu.devinmehringer.project4.controller.exception.IllegalStateTransitionException;
import iu.devinmehringer.project4.statemachine.ActionContext;
import iu.devinmehringer.project4.statemachine.ActionState;
import org.springframework.stereotype.Component;

@Component
public class AbandonedState implements ActionState {

    @Override
    public void implement(ActionContext ctx) {
        throw new IllegalStateTransitionException("ABANDONED", "implement");
    }

    @Override
    public void suspend(ActionContext ctx, String reason) {
        throw new IllegalStateTransitionException("ABANDONED", "suspend");
    }

    @Override
    public void resume(ActionContext ctx) {
        throw new IllegalStateTransitionException("ABANDONED", "resume");
    }

    @Override
    public void complete(ActionContext ctx) {
        throw new IllegalStateTransitionException("ABANDONED", "complete");
    }

    @Override
    public void abandon(ActionContext ctx) {
        throw new IllegalStateTransitionException("ABANDONED", "abandon");
    }

    @Override
    public String name() { return "ABANDONED"; }
}