package t16.components.dialogs;

import javafx.beans.NamedArg;
import javafx.scene.control.Alert;

/**
 * Generic Dialog
 *
 * @author Huw Jones
 * @since 27/02/2017
 */
public class GenericDialog extends Alert {
    /**
     * Creates an alert with the given AlertType (refer to the {@link AlertType}
     * documentation for clarification over which one is most appropriate).
     * <p>
     *
     * @param alertType Type of Alert
     * @param title     Title of Dialog box
     * @param header    Header text of Dialog box
     * @param body      Content of Dialog box
     */
    public GenericDialog(@NamedArg("alertType") AlertType alertType, String title, String header, String body) {
        super(alertType);
        this.setTitle(title);
        this.setHeaderText(header);
        this.setContentText(body);
    }

    /**
     * Creates an alert with the given AlertType (refer to the {@link AlertType}
     * documentation for clarification over which one is most appropriate).
     * <p>
     *
     * @param alertType Type of Alert
     * @param title     Title/Header of Dialog box
     * @param body      Content of Dialog box
     */
    public GenericDialog(@NamedArg("alertType") AlertType alertType, String title, String body) {
        super(alertType);
        this.setTitle(title);
        this.setHeaderText(null);
        this.setContentText(body);
    }
}
