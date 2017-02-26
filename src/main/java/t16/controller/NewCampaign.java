package t16.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

/**
 * New Campaign Controller
 *
 * @author Huw Jones
 * @since 25/02/2017
 */
public class NewCampaign {

    private boolean isCreatingCampaign = false;

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
    //</editor-fold>


    //<editor-fold desc="View Methods">
    @FXML
    private void clickLogBrowseAction(ActionEvent event){
        File file = browseFile("Click Log", event);
        clickLogText.setText(file != null ? file.getAbsolutePath() : "");
    }

    @FXML
    private void impressionLogBrowseAction(ActionEvent event){
        File file = browseFile("Impression Log", event);
        impressionLogText.setText(file != null ? file.getAbsolutePath() : "");
    }

    @FXML
    private void serverLogBrowseAction(ActionEvent event){
        File file = browseFile("Server Log", event);
        serverLogText.setText(file != null ? file.getAbsolutePath() : "");
    }

    @FXML
    private void campaignBrowseAction(ActionEvent event){
        FileChooser fc = new FileChooser();
        fc.setTitle("Save Campaign");
        fc.setInitialDirectory(new File(System.getProperty("user.home")));

        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("H2 (*.h2)", "*.h2");
        fc.getExtensionFilters().add(filter);

        File savePath = fc.showSaveDialog(((Control)event.getSource()).getScene().getWindow());
        campaignSaveText.setText(savePath != null ? savePath.getAbsolutePath() : "");
    }

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
    //</editor-fold>

    private File browseFile(String file, ActionEvent event){
        FileChooser fc = new FileChooser();
        fc.setTitle("Open " + file);
        fc.setInitialDirectory(new File(System.getProperty("user.home")));

        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("CSV Files (*.csv)", "*.csv");
        fc.getExtensionFilters().add(filter);

        return fc.showOpenDialog(((Control)event.getSource()).getScene().getWindow());
    }
}
