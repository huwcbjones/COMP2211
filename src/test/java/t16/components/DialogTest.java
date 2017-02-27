package t16.components;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.junit.Rule;
import org.junit.Test;
import t16.components.dialogs.ConfirmationDialog;
import t16.components.dialogs.ErrorDialog;
import t16.components.dialogs.InfoDialog;
import t16.components.dialogs.WarningDialog;
import t16.components.utils.JavaFXThreadingRule;

import java.util.Optional;

/**
 * Dialog Test
 *
 * @author Huw Jones
 * @since 27/02/2017
 */
public class DialogTest {

    @Rule
    public JavaFXThreadingRule jfxRule = new JavaFXThreadingRule();

    @Test
    public void ErrorDialogTest() throws Exception {
        new ErrorDialog("Title Text", "Header Text", "Body Text").show();
    }

    @Test
    public void ErrorDialogTestNoBody() throws Exception {
        new ErrorDialog("Title Text", "Body Text").show();
    }

    @Test
    public void WarningDialogTest() throws Exception {
        new WarningDialog("Title Text", "Header Text", "Body Text").show();
    }

    @Test
    public void WarningDialogTestNoBody() throws Exception {
        new WarningDialog("Title Text", "Body Text").show();
    }

    @Test
    public void InfoDialogTest() throws Exception {
        new InfoDialog("Title Text", "Header Text", "Body Text").show();
    }

    @Test
    public void InfoDialogTestNoBody() throws Exception {
        new InfoDialog("Title Text", "Body Text").show();
    }

    @Test
    public void ConfirmationTest() throws Exception {
        ConfirmationDialog conf = new ConfirmationDialog(Alert.AlertType.WARNING, "Overwrite File?", "Are you sure you want to overwrite x?", "Overwrite");
        Optional<ButtonType> result = conf.showAndWait();
        result.ifPresent(buttonType ->System.out.println("isAction? " + conf.isAction(buttonType) + "\n" + "isCancel? " + conf.isCancel(buttonType)));
    }
}