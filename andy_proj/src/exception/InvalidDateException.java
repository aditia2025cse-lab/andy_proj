package exception;

// Thrown when a due date is not valid or is before today.
public class InvalidDateException extends Exception {

    private static final long serialVersionUID = 1L;

    // Creates the exception with a friendly message.
    public InvalidDateException(String message) {
        super(message);
    }
}
