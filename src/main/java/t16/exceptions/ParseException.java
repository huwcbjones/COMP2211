package t16.exceptions;

/**
 * A Parse Exception
 *
 * @author Huw Jones
 * @since 06/03/2017
 */
public class ParseException extends java.text.ParseException {
    /**
     * Constructs a ParseException with the specified detail message and
     * offset.
     * A detail message is a String that describes this particular exception.
     *
     * @param s           the detail message
     * @param line        the line where the error is found.
     * @param errorOffset the position where the error is found.
     */
    public ParseException(String s, int line, int errorOffset) {
        super(s, line * errorOffset);
    }
}
