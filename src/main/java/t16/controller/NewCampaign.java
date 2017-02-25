package t16.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import javafx.event.ActionEvent;

/**
 * New Campaign Controller
 *
 * @author Huw Jones
 * @since 25/02/2017
 */
public class NewCampaign {

    private boolean isCreatingCampaign = false;

    @FXML
    private TextField clickLogText;

    @FXML
    private TextField impressionLogText;

    @FXML
    private TextField serverLogText;

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
    private void cancelButtonAction(ActionEvent event){
        if(isCreatingCampaign){
            //TODO: Cancel campaign creation and cleanup
        }
        Stage stage = (Stage)cancelButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void createButtonActive(ActionEvent event){
        //TODO: Create campaign
    }

}
