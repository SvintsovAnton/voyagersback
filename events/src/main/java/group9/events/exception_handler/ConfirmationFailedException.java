package group9.events.exception_handler;

public class ConfirmationFailedException extends RuntimeException {

    public ConfirmationFailedException(String message) {
        super(message);
    }
}