package iu.devinmehringer.project4.controller.exception;

public class IllegalStateTransitionException extends RuntimeException {
    public IllegalStateTransitionException(String message, String resume) {
        super(message);
    }
}
