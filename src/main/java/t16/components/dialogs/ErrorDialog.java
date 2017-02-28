package t16.components.dialogs;

/**
 * Alert Dialog
 *
 * @author Huw Jones
 * @since 27/02/2017
 */
public class ErrorDialog extends GenericDialog {

    /**
     * Type of the Dialog
     */
    private static final AlertType type = AlertType.ERROR;

    /**
     * Creates an Error Dialog for displaying errors
     *
     * @param title  Title of Dialog
     * @param header Header of Dialog
     * @param body   Content of Dialog
     */
    public ErrorDialog(String title, String header, String body) {
        super(type, title, header, body);
    }

    /**
     * Creates an Error Dialog for displaying errors
     *
     * @param title Title/Header of Dialog
     * @param body  Content of Dialog
     */
    public ErrorDialog(String title, String body) {
        super(type, title, body);
    }
}
