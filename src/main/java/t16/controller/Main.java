package t16.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import t16.components.dialogs.ExceptionDialog;
import t16.model.Campaign;

import java.io.File;
import java.util.UUID;

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

    public Main() {
    }


    public void displayCampaign(UUID campaignID) {

    }

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
        //TODO: Open a campaign
        File campaignDatabase = browseCampaign(event);
        if (!campaignDatabase.canRead() || !campaignDatabase.canWrite()) {
            //TODO: Show alert dialog
            return;
        }
        Campaign campaign = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/dashboard.fxml"));
            Parent scene = loader.load();
            Dashboard controller = loader.getController();
            controller.setCampaign(campaign);

            Stage stage = new Stage();
            stage.setTitle(campaign.getName() + " - Ad Dashboard");
            stage.setScene(new Scene(scene, 500, 400));
            stage.show();
        } catch (Exception e) {
            ExceptionDialog dialog = new ExceptionDialog("Load error!", "Failed to load campaign.", e);
            dialog.showAndWait();
        }
    }

    @FXML
    private void exitButtonAction(ActionEvent event) {
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
    }

    private File browseCampaign(ActionEvent event){
        FileChooser fc = new FileChooser();
        fc.setTitle("Open Campaign");
        fc.setInitialDirectory(new File(System.getProperty("user.home")));

        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Ad Dashboard Database (*.h2)", "*.h2");
        fc.getExtensionFilters().add(filter);

        return fc.showOpenDialog(((Control)event.getSource()).getScene().getWindow());
    }
}
