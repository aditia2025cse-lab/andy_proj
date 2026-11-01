package exception;

// Thrown when a student tries to add the same subject name twice.
public class DuplicateSubjectException extends Exception {

    private static final long serialVersionUID = 1L;

    // Creates the exception with a friendly message.
    public DuplicateSubjectException(String message) {
        super(message);
    }
}
