package t16.exceptions;

/**
 * Database Creation Exception
 *
 * @author Huw Jones
 * @since 01/03/2017
 */
public class DatabaseCreationException extends DatabaseException {

    /**
     * Constructs a new exception with the specified detail message.  The
     * cause is not initialized, and may subsequently be initialized by
     * a call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public DatabaseCreationException(String message, Throwable t) {
        super(message, t);
    }
}
