package t16.exceptions;

/**
 * {DESCRIPTION}
 *
 * @author Huw Jones
 * @since 01/03/2017
 */
public class DatabaseConnectionException extends DatabaseException {


    public DatabaseConnectionException(String message) {
        super(message);
    }

    public DatabaseConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
