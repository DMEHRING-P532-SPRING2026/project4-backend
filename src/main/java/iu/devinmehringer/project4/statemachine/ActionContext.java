package iu.devinmehringer.project4.statemachine;

import iu.devinmehringer.project4.manager.ActionManager;
import iu.devinmehringer.project4.model.plan.ProposedAction;

public class ActionContext {

    private final ProposedAction action;
    private final ActionManager actionManager;

    public ActionContext(ProposedAction action, ActionManager actionManager) {
        this.action = action;
        this.actionManager = actionManager;
    }

    public ProposedAction getAction() { return action; }

    public ActionManager getActionManager() { return actionManager; }
}