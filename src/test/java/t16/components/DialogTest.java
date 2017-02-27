package t16.components;

import org.junit.Rule;
import org.junit.Test;
import t16.components.dialogs.ErrorDialog;
import t16.components.dialogs.InfoDialog;
import t16.components.dialogs.WarningDialog;
import t16.components.utils.JavaFXThreadingRule;

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

}