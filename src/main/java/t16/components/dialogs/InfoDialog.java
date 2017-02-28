package t16.components.dialogs;

/**
 * Info Dialog
 *
 * @author Huw Jones
 * @since 27/02/2017
 */
public class InfoDialog extends GenericDialog {

    /**
     * Type of the Dialog
     */
    private static final AlertType type = AlertType.INFORMATION;

    /**
     * Creates an Info Dialog for displaying info
     *
     * @param title  Title of Dialog
     * @param header Header of Dialog
     * @param body   Content of Dialog
     */
    public InfoDialog(String title, String header, String body) {
        super(type, title, header, body);
    }

    /**
     * Creates an Info Dialog for displaying info
     *
     * @param title Title/Header of Dialog
     * @param body  Content of Dialog
     */
    public InfoDialog(String title, String body) {
        super(type, title, body);
    }
}
