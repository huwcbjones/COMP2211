package t16.controller;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import t16.AdDashboard;
import t16.components.dialogs.ConfirmationDialog;
import t16.components.dialogs.ErrorDialog;
import t16.components.dialogs.ExceptionDialog;
import t16.components.dialogs.GenericDialog;
import t16.model.Campaign;

import java.io.File;
import java.util.Optional;

/**
 * Controller for Main View
 *
 * @author Huw Jones
 * @since 25/02/2017
 */
public class Main {

    protected static Logger log = LogManager.getLogger(Main.class);
    private static Main main;

    private Task task = null;

    @FXML
    private Button newCampaignButton;
    @FXML
    private Button openCampaignButton;
    @FXML
    private Button exitButton;

    @FXML
    private ProgressBar progressBar;

    @FXML
    public void initialize() {
        main = this;
    }

    @FXML
    private void createNewCampaignButtonAction(ActionEvent event) {
        Parent root;
        try {
            root = FXMLLoader.load(getClass().getResource("/newCampaign.fxml"));
            Stage stage = new Stage();
            stage.setTitle("New Campaign - Ad Dashboard");
            stage.setScene(new Scene(root, 600, 450));
            stage.setResizable(false);
            stage.showAndWait();
        } catch (Exception e) {
            ExceptionDialog dialog = new ExceptionDialog("Create campaign error!", "Failed to create campaign.", e);
            dialog.showAndWait();
        }
    }

    @FXML
    private void openCampaignButtonAction(ActionEvent event) {
        doWork();
        File campaignDatabase = browseCampaign(event);

        if (campaignDatabase == null) {
            stopWork();
            return;
        }

        if (!(campaignDatabase.canRead() && campaignDatabase.canWrite())) {
            ErrorDialog dialog = new ErrorDialog(
                    "Open Campaign Error!",
                    "Failed to open campaign",
                    "Campaign file was not read/writable.\nTo open this campaign, please ensure the file is read/write enabled."
            );
            dialog.showAndWait();
            stopWork();
            return;
        }

        Task<Campaign> openTask = new Task<Campaign>() {
            @Override
            protected Campaign call() throws Exception {
                return AdDashboard.getDataController().openCampaign(campaignDatabase);
            }
        };

        openTask.setOnSucceeded(e -> {
            openCampaign(openTask.getValue());
        });
        openTask.setOnFailed(e -> {
            log.error("Open task failed!");
            GenericDialog dialog;
            if (e.getSource().getException() != null) {
                log.catching(e.getSource().getException());
                dialog = new ExceptionDialog(
                        "Open Campaign Error!",
                        "Failed to open campaign",
                        e.getSource().getException()
                );

            } else {
                dialog = new ErrorDialog(
                        "Open Campaign Error!",
                        "Failed to open campaign",
                        "An unknown error occurred whilst opening a campaign."
                );
            }
            dialog.showAndWait();
            stopWork();
        });

        AdDashboard.getWorkerPool().queueTask(openTask);
    }

    private void doWork() {
        newCampaignButton.setDisable(true);
        openCampaignButton.setDisable(true);
        progressBar.setVisible(true);
    }

    private File browseCampaign(ActionEvent event) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Open Campaign");
        fc.setInitialDirectory(new File(System.getProperty("user.home")));

        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Ad Dashboard Database (*.h2.db)", "*.h2.db");
        fc.getExtensionFilters().add(filter);

        return fc.showOpenDialog(((Control) event.getSource()).getScene().getWindow());
    }

    private void stopWork() {
        newCampaignButton.setDisable(false);
        openCampaignButton.setDisable(false);
        progressBar.setVisible(false);
    }

    public static void openCampaign(Campaign campaign) {
        try {
            FXMLLoader loader = new FXMLLoader(main.getClass().getResource("/dashboard.fxml"));
            Parent parent = loader.load();
            Dashboard controller = loader.getController();
            controller.setCampaign(campaign);

            Scene scene = new Scene(parent, 1600, 800);

            Stage stage = new Stage();
            stage.setMinHeight(720);
            stage.setMinWidth(1280);
            stage.setTitle(campaign.getName() + " - Ad Dashboard");
            stage.setScene(scene);

            // Nasty hack to get main window to close when a campaign is opened
            main.stopWork();
            ((Stage) main.progressBar.getScene().getWindow()).close();

            stage.show();
            controller.setScene(scene);
        } catch (Exception e) {
            log.catching(e);
            ExceptionDialog dialog = new ExceptionDialog("Load error!", "Failed to load campaign.", e);
            dialog.showAndWait();
        }

    }

    @FXML
    private void exitButtonAction(ActionEvent event) {
        ConfirmationDialog confirm = new ConfirmationDialog(
                Alert.AlertType.CONFIRMATION,
                "Are you sure you want to exit?",
                "Are you sure you want to exit?",
                "Exit");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && confirm.isAction(result.get())) {
            Platform.exit();
        }
    }
}
