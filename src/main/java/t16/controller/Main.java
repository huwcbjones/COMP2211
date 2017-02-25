package t16.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import javafx.event.ActionEvent;
import java.io.IOException;
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
            stage.setScene(new Scene(root, 500, 400));
            stage.show();
        } catch (IOException e) {
            //TODO: Handle exception appropriately
            e.printStackTrace();
        }
    }

    @FXML
    private void openCampaignButtonAction(ActionEvent event) {
        //TODO: Open a campaign (using dashboard.fxml)
        Parent root;
        try {
            root = FXMLLoader.load(getClass().getResource("/dashboard.fxml"));
            Stage stage = new Stage();
            stage.setTitle("");
            stage.setScene(new Scene(root, 500, 400));
            stage.show();
        } catch (IOException e) {
            //TODO: Handle exception appropriately
            e.printStackTrace();
        }
    }

    @FXML
    private void exitButtonAction(ActionEvent event) {
        Stage stage = (Stage)exitButton.getScene().getWindow();
        stage.close();
    }
}
