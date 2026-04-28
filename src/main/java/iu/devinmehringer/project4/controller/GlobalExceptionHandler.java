package iu.devinmehringer.project4.controller;

import iu.devinmehringer.project4.controller.exception.ProtocolCannotSelfReference;
import iu.devinmehringer.project4.controller.exception.ProtocolNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ProtocolNotFoundException.class)
    public ResponseEntity<String> handleProtocolNotFoundException(ProtocolNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(ProtocolCannotSelfReference.class)
    public ResponseEntity<String> handleProtocolCannotSelfReference(ProtocolCannotSelfReference e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
}
