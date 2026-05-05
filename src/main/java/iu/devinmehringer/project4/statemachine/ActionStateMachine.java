package iu.devinmehringer.project4.statemachine;

import iu.devinmehringer.project4.model.plan.ProposedAction;
import org.springframework.stereotype.Component;

@Component
public class ActionStateMachine {

    private final ProposedState proposedState;
    private final PendingApprovalState pendingApprovalState;
    private final SuspendedState suspendedState;
    private final InProgressState inProgressState;
    private final CompletedState completedState;
    private final ReopenedState reopenedState;
    private final AbandonedState abandonedState;

    public ActionStateMachine(
            ProposedState proposedState,
            PendingApprovalState pendingApprovalState,
            SuspendedState suspendedState,
            InProgressState inProgressState,
            CompletedState completedState,
            ReopenedState reopenedState,
            AbandonedState abandonedState) {
        this.proposedState = proposedState;
        this.pendingApprovalState = pendingApprovalState;
        this.suspendedState = suspendedState;
        this.inProgressState = inProgressState;
        this.completedState = completedState;
        this.reopenedState = reopenedState;
        this.abandonedState = abandonedState;
    }

    private ActionState getHandler(ProposedAction action) {
        return switch (action.getState()) {
            case PROPOSED -> proposedState;
            case PENDING_APPROVAL -> pendingApprovalState;
            case SUSPENDED -> suspendedState;
            case IN_PROGRESS -> inProgressState;
            case COMPLETED -> completedState;
            case REOPENED -> reopenedState;
            case ABANDONED -> abandonedState;
        };
    }

    public void implement(ProposedAction action, ActionContext ctx) {
        getHandler(action).implement(ctx);
    }

    public void submitForApproval(ProposedAction action, ActionContext ctx) {
        getHandler(action).submitForApproval(ctx);
    }

    public void approve(ProposedAction action, ActionContext ctx) {
        getHandler(action).approve(ctx);
    }

    public void reject(ProposedAction action, ActionContext ctx) {
        getHandler(action).reject(ctx);
    }

    public void reopen(ProposedAction action, ActionContext ctx) {
        getHandler(action).reopen(ctx);
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