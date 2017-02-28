package t16.components.dialogs;

import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Exception Dialog
 * Displays exceptions
 *
 * @author Huw Jones
 * @since 26/02/2017
 */
public class ExceptionDialog extends GenericDialog {

    private java.lang.Exception exception;

    /**
     * Creates an Exception Dialog.
     *
     * @param title  Title of dialog
     * @param header Header of dialog
     * @param ex     Exception to display
     */
    public ExceptionDialog(String title, String header, java.lang.Exception ex) {
        super(AlertType.ERROR, title, header, ex.getLocalizedMessage());
        this.exception = ex;
        init();
    }

    /**
     * Creates an Exception Dialog
     *
     * @param title Title of dialog
     * @param ex    Exception to display
     */
    public ExceptionDialog(String title, java.lang.Exception ex) {
        this(title, title, ex);
    }

    /**
     * Creates an Exception dialog
     *
     * @param ex Exception to display
     */
    public ExceptionDialog(java.lang.Exception ex) {
        this("An exception occurred!", ex);
    }

    private void init() {
        // Print stacktrace to PrintWriter
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        exception.printStackTrace(pw);

        // Display stacktrace in a text area
        Label stacktraceLabel = new Label("The stacktrace was:");
        TextArea stacktraceArea = new TextArea(sw.toString());
        stacktraceArea.setEditable(false);
        stacktraceArea.setWrapText(true);

        // Set layout
        stacktraceArea.setMaxHeight(Double.MAX_VALUE);
        stacktraceArea.setMaxWidth(Double.MAX_VALUE);
        GridPane.setVgrow(stacktraceArea, Priority.ALWAYS);
        GridPane.setHgrow(stacktraceArea, Priority.ALWAYS);

        GridPane content = new GridPane();
        content.setMaxWidth(Double.MAX_VALUE);
        content.add(stacktraceLabel, 0, 0);
        content.add(stacktraceArea, 0, 1);

        this.getDialogPane().setExpandableContent(content);
    }

}
