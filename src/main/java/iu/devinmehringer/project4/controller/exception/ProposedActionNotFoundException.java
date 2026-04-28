package iu.devinmehringer.project4.controller.exception;

public class ProposedActionNotFoundException extends RuntimeException {
    public ProposedActionNotFoundException(String message) {
        super(message);
    }
}
