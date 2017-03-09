package t16.controller;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import t16.AdDashboard;
import t16.components.dialogs.ConfirmationDialog;
import t16.components.dialogs.ExceptionDialog;
import t16.model.Campaign;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * New Campaign Controller
 *
 * @author Huw Jones
 * @since 25/02/2017
 */
public class NewCampaign {

    private boolean isCreatingCampaign = false;
    private boolean isZipCreate = true;

    private ExtensionFilter CSVfilter = new ExtensionFilter("CSV Files (*.csv)", "*.csv");
    private ExtensionFilter ZIPfilter = new ExtensionFilter("ZIP File (*.zip)", "*.zip");

    // TODO: Add zip option to GUI

    //<editor-fold desc="View Controls">
    @FXML
    private ToggleGroup creationMethodGroup;

    @FXML
    private TextField zipFileText;

    @FXML
    private TextField clickLogText;

    @FXML
    private TextField impressionLogText;

    @FXML
    private TextField serverLogText;

    @FXML
    private TextField campaignSaveText;

    @FXML
    private Button zipFileBrowse;

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

    private List<Control> toggleControls = new ArrayList<>();

    @FXML
    public void initialize() {
        toggleControls.add(zipFileText);
        toggleControls.add(zipFileBrowse);
        toggleControls.add(clickLogText);
        toggleControls.add(clickLogBrowseButton);
        toggleControls.add(impressionLogText);
        toggleControls.add(impressionLogBrowseButton);
        toggleControls.add(serverLogText);
        toggleControls.add(serverLogBrowseButton);

        creationMethodGroup.selectedToggleProperty().addListener(e -> {
            toggleCreationMethod();
            isZipCreate = !isZipCreate;
        });
    }

    private void toggleCreationMethod() {
        toggleControls.forEach(e -> e.setDisable(!e.isDisabled()));
    }

    //<editor-fold desc="View Methods">
    @FXML
    private void zipFileBrowseAction(ActionEvent event) {
        File file = browseFile("Data Zip", event, ZIPfilter);
        zipFileText.setText(file != null ? file.getAbsolutePath() : "");
    }

    private File browseFile(String file, ActionEvent event, ExtensionFilter filter) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Open " + file);
        fc.setInitialDirectory(new File(System.getProperty("user.home")));

        fc.getExtensionFilters().add(filter);

        return fc.showOpenDialog(((Control) event.getSource()).getScene().getWindow());
    }

    @FXML
    private void clickLogBrowseAction(ActionEvent event) {
        File file = browseFile("Click Log", event, CSVfilter);
        clickLogText.setText(file != null ? file.getAbsolutePath() : "");
    }

    @FXML
    private void impressionLogBrowseAction(ActionEvent event) {
        File file = browseFile("Impression Log", event, CSVfilter);
        impressionLogText.setText(file != null ? file.getAbsolutePath() : "");
    }

    @FXML
    private void serverLogBrowseAction(ActionEvent event) {
        File file = browseFile("Server Log", event, CSVfilter);
        serverLogText.setText(file != null ? file.getAbsolutePath() : "");
    }

    @FXML
    private void campaignBrowseAction(ActionEvent event) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Save Campaign");
        fc.setInitialDirectory(new File(System.getProperty("user.home")));

        ExtensionFilter filter = new ExtensionFilter("H2 (*.h2.db)", "*.h2.db");
        fc.getExtensionFilters().add(filter);

        File savePath = fc.showSaveDialog(((Control) event.getSource()).getScene().getWindow());
        campaignSaveText.setText(savePath != null ? savePath.getAbsolutePath() : "");
    }
    //</editor-fold>

    @FXML
    private void cancelButtonAction(ActionEvent event) {
        ConfirmationDialog confirm = new ConfirmationDialog(
                Alert.AlertType.CONFIRMATION,
                "Cancel Campaign Creation?",
                "Are you sure you want to cancel the campaign creation?",
                "Cancel Campaign Creation");
        Optional<ButtonType> result = confirm.showAndWait();
        if (!(result.isPresent() && confirm.isAction(result.get()))) {
            return;
        }
        if (!isCreatingCampaign) {
            // Close window
            ((Stage) ((Control) event.getSource()).getScene().getWindow()).close();
        } else {
            //TODO: Cancel campaign creation and cleanup
        }
    }

    @FXML
    private void createButtonActive(ActionEvent event) throws IOException {
        doWork();
        Task<Campaign> createTask;
        File databaseFile = new File(campaignSaveText.getText());
        if (isZipCreate) {
            File zipFile = new File(zipFileText.getText());
            createTask = new Task<Campaign>() {

                @Override
                protected Campaign call() throws Exception {
                    return AdDashboard.getDataController().createCampaign(zipFile, databaseFile);
                }
            };
        } else {
            File clickFile = new File(clickLogText.getText());
            File impressionFile = new File(impressionLogText.getText());
            File serverFile = new File(serverLogText.getText());

            createTask = new Task<Campaign>() {
                @Override
                protected Campaign call() throws Exception {
                    return AdDashboard.getDataController().createCampaign(clickFile, impressionFile, serverFile, databaseFile);
                }
            };
        }

        createTask.setOnSucceeded(e -> {
            Main.openCampaign(createTask.getValue());
            stopWork();
            ((Stage) ((Control) event.getSource()).getScene().getWindow()).close();
        });
        createTask.setOnFailed(e -> {
            ExceptionDialog dialog = new ExceptionDialog(
                    "Failed to create campaign.",
                    "An exception occurred whilst creating the campaign.",
                    e.getSource().getException()
            );
            stopWork();
            dialog.showAndWait();
        });

        AdDashboard.getWorkerPool().queueTask(createTask);
    }

    private void doWork() {
        createButton.setDisable(true);
        progressBar.setVisible(true);
    }

    private void stopWork() {
        createButton.setDisable(false);
        progressBar.setVisible(false);
    }
}
