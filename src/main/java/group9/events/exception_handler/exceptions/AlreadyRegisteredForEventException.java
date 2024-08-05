package group9.events.exception_handler.exceptions;


public class AlreadyRegisteredForEventException extends RuntimeException {
    public AlreadyRegisteredForEventException(String message) {
        super(message);
    }
}