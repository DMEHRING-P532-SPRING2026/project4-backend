package iu.devinmehringer.project4.controller.exception;

public class ProtocolNotFoundException extends RuntimeException {
    public ProtocolNotFoundException(String message) {
        super(message);
    }
}
