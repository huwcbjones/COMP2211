package t16.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import t16.components.dialogs.ConfirmationDialog;
import t16.model.Campaign;
import t16.model.Database;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

/**
 * New Campaign Controller
 *
 * @author Huw Jones
 * @since 25/02/2017
 */
public class NewCampaign {

    private boolean isCreatingCampaign = false;
    private boolean isZipCreate = false;

    // TODO: Add zip option to GUI

    //<editor-fold desc="View Controls">
    @FXML
    private TextField clickLogText;

    @FXML
    private TextField impressionLogText;

    @FXML
    private TextField serverLogText;

    @FXML
    private TextField campaignSaveText;

    @FXML
    private Button clickLogBrowseButton;

    @FXML
    private Button impressionLogBrowseButton;

    @FXML
    private Button serverLogBrowseButton;

    @FXML
    private Button cancelButton;

    @FXML
    private Button createButton;

    @FXML
    private ProgressBar progressBar;
    //</editor-fold>


    //<editor-fold desc="View Methods">
    @FXML
    private void clickLogBrowseAction(ActionEvent event) {
        File file = browseFile("Click Log", event);
        clickLogText.setText(file != null ? file.getAbsolutePath() : "");
    }

    @FXML
    private void impressionLogBrowseAction(ActionEvent event) {
        File file = browseFile("Impression Log", event);
        impressionLogText.setText(file != null ? file.getAbsolutePath() : "");
    }

    @FXML
    private void serverLogBrowseAction(ActionEvent event) {
        File file = browseFile("Server Log", event);
        serverLogText.setText(file != null ? file.getAbsolutePath() : "");
    }

    @FXML
    private void campaignBrowseAction(ActionEvent event) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Save Campaign");
        fc.setInitialDirectory(new File(System.getProperty("user.home")));

        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("H2 (*.h2)", "*.h2");
        fc.getExtensionFilters().add(filter);

        File savePath = fc.showSaveDialog(((Control) event.getSource()).getScene().getWindow());
        campaignSaveText.setText(savePath != null ? savePath.getAbsolutePath() : "");
    }

    @FXML
    private void cancelButtonAction(ActionEvent event) {
        ConfirmationDialog confirm = new ConfirmationDialog(
                Alert.AlertType.CONFIRMATION,
                "Cancel Campaign Creation?",
                "Are you sure you want to cancel the campaign creation?",
                "Cancel Campaign Creation");
        Optional<ButtonType> result = confirm.showAndWait();
        if(!(result.isPresent() && confirm.isAction(result.get()))) {
            return;
        }
        if (isCreatingCampaign) {
            //TODO: Cancel campaign creation and cleanup
        } else {
            Platform.exit();
        }
    }

    @FXML
    private void createButtonActive(ActionEvent event) throws IOException {
        Database database = Database.database;
        Campaign campaign = null;

        progressBar.setVisible(true);

        //TODO: Create campaign
        if (isZipCreate) {
            campaign = database.createCampaign(new File("/path/to/database.h2"), new File(campaignSaveText.getText()));
        } else {
            campaign = database.createCampaign(new File(clickLogText.getText()), new File(impressionLogText.getText()), new File(serverLogText.getText()), new File(campaignSaveText.getText()));
        }

        progressBar.setVisible(false);
    }
    //</editor-fold>

    private File browseFile(String file, ActionEvent event) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Open " + file);
        fc.setInitialDirectory(new File(System.getProperty("user.home")));

        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("CSV Files (*.csv)", "*.csv");
        fc.getExtensionFilters().add(filter);

        return fc.showOpenDialog(((Control) event.getSource()).getScene().getWindow());
    }
}
