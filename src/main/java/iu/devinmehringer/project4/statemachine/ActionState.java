package iu.devinmehringer.project4.statemachine;

public interface ActionState {
    void implement(ActionContext ctx);
    void suspend(ActionContext ctx, String reason);
    void resume(ActionContext ctx);
    void complete(ActionContext ctx);
    void abandon(ActionContext ctx);
    String name();
}
