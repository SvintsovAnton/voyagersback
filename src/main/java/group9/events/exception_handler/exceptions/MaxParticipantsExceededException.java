package group9.events.exception_handler.exceptions;

public class MaxParticipantsExceededException extends RuntimeException {
    public MaxParticipantsExceededException(String message) {
        super(message);
    }
}
