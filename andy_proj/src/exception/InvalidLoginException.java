package exception;

// Thrown when the email or password is wrong.
public class InvalidLoginException extends Exception {

    private static final long serialVersionUID = 1L;

    // Creates the exception with a friendly message.
    public InvalidLoginException(String message) {
        super(message);
    }
}
