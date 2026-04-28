package iu.devinmehringer.project4.model.resource;

import iu.devinmehringer.project4.model.plan.ProposedAction;
import iu.devinmehringer.project4.statemachine.ActionContext;
import iu.devinmehringer.project4.statemachine.ActionState;
import iu.devinmehringer.project4.statemachine.ProposedState;
import org.springframework.stereotype.Service;

@Service
public class ActionStateMachine {

    private final ProposedState proposedState;
    private final SuspendedState suspendedState;
    private final InProgressState inProgressState;
    private final CompletedState completedState;
    private final AbandonedState abandonedState;

    public ActionStateMachine(
            ProposedState proposedState,
            SuspendedState suspendedState,
            InProgressState inProgressState,
            CompletedState completedState,
            AbandonedState abandonedState) {
        this.proposedState = proposedState;
        this.suspendedState = suspendedState;
        this.inProgressState = inProgressState;
        this.completedState = completedState;
        this.abandonedState = abandonedState;
    }

    private ActionState getHandler(ProposedAction action) {
        return switch (action.getState()) {
            case PROPOSED -> proposedState;
            case SUSPENDED -> suspendedState;
            case IN_PROGRESS -> inProgressState;
            case COMPLETED -> completedState;
            case ABANDONED -> abandonedState;
        };
    }

    public void implement(ProposedAction action, ActionContext ctx) {
        getHandler(action).implement(ctx);
    }

    public void suspend(ProposedAction action, ActionContext ctx, String reason) {
        getHandler(action).suspend(ctx, reason);
    }

    public void resume(ProposedAction action, ActionContext ctx) {
        getHandler(action).resume(ctx);
    }

    public void complete(ProposedAction action, ActionContext ctx) {
        getHandler(action).complete(ctx);
    }

    public void abandon(ProposedAction action, ActionContext ctx) {
        getHandler(action).abandon(ctx);
    }
}