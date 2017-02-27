package t16.components.dialogs;

/**
 * Warning Dialog
 *
 * @author Huw Jones
 * @since 27/02/2017
 */
public class WarningDialog extends GenericDialog {

    /**
     * Type of the Dialog
     */
    private static final AlertType type = AlertType.WARNING;

    /**
     * Creates a Warning Dialog for displaying warnings
     *
     * @param title  Title of Dialog
     * @param header Header of Dialog
     * @param body   Content of Dialog
     */
    public WarningDialog(String title, String header, String body) {
        super(type, title, header, body);
    }

    /**
     * Creates a Warning Dialog for displaying warnings
     *
     * @param title Title/Header of Dialog
     * @param body  Content of Dialog
     */
    public WarningDialog(String title, String body) {
        super(type, title, body);
    }
}
