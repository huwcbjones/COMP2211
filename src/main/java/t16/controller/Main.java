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

    @FXML
    private Button newCampaignButton;

    @FXML
    private Button openCampaignButton;
    @FXML
    private Button exitButton;

    @FXML
    private void createNewCampaignButtonAction(ActionEvent event) {
        //TODO: Create a new campaign
        Parent root;
        try {
            root = FXMLLoader.load(getClass().getResource("/newCampaign.fxml"));
            Stage stage = new Stage();
            stage.setTitle("New Campaign - Ad Dashboard");
            stage.setScene(new Scene(root, 550, 400));
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

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/dashboard.fxml"));
                Parent scene = loader.load();
                Dashboard controller = loader.getController();
                controller.setCampaign(campaign);
                controller.setScene(new Scene(scene));

                Stage stage = new Stage();
                stage.setTitle(campaign.getName() + " - Ad Dashboard");
                stage.setScene(new Scene(scene, 1280, 720));
                stage.show();

                // Close Main Window
                ((Stage)((Control)event.getSource()).getScene().getWindow()).close();

            } catch (Exception e) {
                ExceptionDialog dialog = new ExceptionDialog("Load error!", "Failed to load campaign.", e);
                dialog.showAndWait();
            }
        } catch (Exception e) {
            ErrorDialog dialog = new ErrorDialog(
                    "Open Campaign Error!",
                    "Failed to open campaign",
                    e.getMessage()
            );
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
        if(result.isPresent() && confirm.isAction(result.get())) {
            Platform.exit();
        }
    }

    private File browseCampaign(ActionEvent event) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Open Campaign");
        fc.setInitialDirectory(new File(System.getProperty("user.home")));

        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Ad Dashboard Database (*.h2)", "*.h2");
        fc.getExtensionFilters().add(filter);

        return fc.showOpenDialog(((Control) event.getSource()).getScene().getWindow());
    }

    private Campaign loadCampaign(File campaignDatabase) {
        Database database = Database.database;
        //return database.loadCampaign(campaignDatabase);
        Campaign c = new Campaign();
        c.setName("Test");
        return c;
    }
}
