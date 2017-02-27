package t16.components.dialogs;

import javafx.beans.NamedArg;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;

/**
 * Confirmation Dialog
 *
 * @author Huw Jones
 * @since 27/02/2017
 */
public class ConfirmationDialog extends GenericDialog {

    private ButtonType action;
    private ButtonType cancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);

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
    public ConfirmationDialog(@NamedArg("alertType") AlertType alertType, String title, String header, String body, String command) {
        super(alertType, title, header, body);
        setAction(command);

        this.getButtonTypes().setAll(cancel, action);
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
    public ConfirmationDialog(@NamedArg("alertType") AlertType alertType, String title, String body, String command) {
        this(alertType, title, null, body, command);
    }

    public void setAction(String command){
        this.action = new ButtonType(command, ButtonData.APPLY);
    }

    /**
     * Returns whether a button type was cancel
     * @param button Button type
     * @return
     */
    public boolean isCancel(ButtonType button){
        return button.equals(cancel);
    }

    public boolean isAction(ButtonType button){
        return button.equals(action);
    }
}
