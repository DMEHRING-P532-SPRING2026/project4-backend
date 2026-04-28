package iu.devinmehringer.project4.controller.exception;

public class ProtocolCannotSelfReference extends RuntimeException {
    public ProtocolCannotSelfReference(String message) {
        super(message);
    }
}
