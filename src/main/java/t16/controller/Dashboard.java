package t16.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import t16.components.dialogs.ConfirmationDialog;
import t16.model.Campaign;

import java.util.Optional;

/**
 * Dashboard Controller
 *
 * @author Huw Jones
 * @since 25/02/2017
 */
public class Dashboard {

    private Scene scene = null;
    private Campaign campaign = null;

    //<editor-fold desc="View Controls">
    @FXML
    private Label campaignName;

    @FXML
    private Button clicksButton;

    @FXML
    private Button impressionsButton;

    @FXML
    private Button uniqueButton;

    @FXML
    private Button bouncesButton;

    @FXML
    private Button conversionsButton;

    @FXML
    private Button clickThroughsButton;
    //</editor-fold>

    //<editor-fold desc="View Methods">
    @FXML
    private void viewClicks(ActionEvent event) {

    }

    @FXML
    private void viewImpressions(ActionEvent event) {

    }

    @FXML
    private void viewUnique(ActionEvent event) {

    }

    @FXML
    private void viewBounces(ActionEvent event) {

    }

    @FXML
    private void viewConversion(ActionEvent event) {

    }

    @FXML
    private void viewClickThrough(ActionEvent event) {

    }
    //</editor-fold>

    @FXML
    public void initialize() {
        if (campaign != null) campaignName.setText(campaign.getName());
        scene.getWindow().setOnCloseRequest(e -> {
            ConfirmationDialog confirm = new ConfirmationDialog(
                    Alert.AlertType.CONFIRMATION,
                    "Exit Ad Dashboard?",
                    "Are you sure you want to exit " + campaign.getName() + " Dashboard?",
                    "Exit " + campaign.getName());
            Optional<ButtonType> result = confirm.showAndWait();
            if(result.isPresent() && confirm.isAction(result.get())){
                Platform.exit();
            }
        });
    }


    /**
     * Returns the Campaign
     *
     * @return Campaign
     */
    public Campaign getCampaign() {
        return campaign;
    }

    /**
     * Set the campaign of the view, if the Campaign has not already been set
     *
     * @param campaign Campaign to view
     */
    public void setCampaign(Campaign campaign) {
        if (this.campaign != null) this.campaign = campaign;
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }
}
