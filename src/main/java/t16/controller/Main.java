package t16.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Control;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import t16.components.dialogs.ConfirmationDialog;
import t16.components.dialogs.ErrorDialog;
import t16.components.dialogs.ExceptionDialog;
import t16.model.Campaign;
import t16.model.Database;

import java.io.File;
import java.util.Optional;

/**
 * Controller for Main View
 *
 * @author Huw Jones
 * @since 25/02/2017
 */
public class Main {

    private static Class thisClass;
    @FXML
    private Button newCampaignButton;
    @FXML
    private Button openCampaignButton;
    @FXML
    private Button exitButton;

    public static void openCampaign(Campaign campaign) {
        try {
            FXMLLoader loader = new FXMLLoader(thisClass.getResource("/dashboard.fxml"));
            Parent parent = loader.load();
            Dashboard controller = loader.getController();
            controller.setCampaign(campaign);

            Scene scene = new Scene(parent, 1280, 720);
            controller.setScene(scene);

            Stage stage = new Stage();
            stage.setTitle(campaign.getName() + " - Ad Dashboard");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            ExceptionDialog dialog = new ExceptionDialog("Load error!", "Failed to load campaign.", e);
            dialog.showAndWait();
        }

    }

    @FXML
    public void initialize() {
        thisClass = getClass();
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
            stage.show();
        } catch (Exception e) {
            ExceptionDialog dialog = new ExceptionDialog("Create campaign error!", "Failed to create campaign.", e);
            dialog.showAndWait();
        }
    }

    @FXML
    private void openCampaignButtonAction(ActionEvent event) {
        File campaignDatabase = browseCampaign(event);

        if (campaignDatabase == null) return;

        if (!(campaignDatabase.canRead() && campaignDatabase.canWrite())) {
            ErrorDialog dialog = new ErrorDialog(
                    "Open Campaign Error!",
                    "Failed to open campaign",
                    "Campaign file was not read/writable.\nTo open this campaign, please ensure the file is read/write enabled."
            );
            dialog.showAndWait();
            return;
        }

        try {
            Campaign campaign = loadCampaign(campaignDatabase);
            openCampaign(campaign);

            // Close Main Window
            ((Stage) ((Control) event.getSource()).getScene().getWindow()).close();

        } catch (Exception e) {
            ErrorDialog dialog = new ErrorDialog(
                    "Open Campaign Error!",
                    "Failed to open campaign",
                    e.getMessage()
            );
            e.printStackTrace();
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

    private File browseCampaign(ActionEvent event) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Open Campaign");
        fc.setInitialDirectory(new File(System.getProperty("user.home")));

        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Ad Dashboard Database (*.h2.db)", "*.h2.db");
        fc.getExtensionFilters().add(filter);

        return fc.showOpenDialog(((Control) event.getSource()).getScene().getWindow());
    }

    private Campaign loadCampaign(File campaignDatabase) {
        Database database = Database.InitialiseDatabase();
        return database.loadCampaign(campaignDatabase);
    }
}
