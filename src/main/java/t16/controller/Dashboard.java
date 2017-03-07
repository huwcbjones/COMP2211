package t16.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import t16.components.dialogs.ConfirmationDialog;
import t16.components.dialogs.ExceptionDialog;
import t16.model.Campaign;
import t16.model.Database;

import java.sql.SQLException;
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

    @FXML
    private BorderPane chartPane;

    @FXML
    private DatePicker startDate;

    @FXML
    private DatePicker endDate;
    //</editor-fold>

    //<editor-fold desc="View Methods">
    @FXML
    private void viewClicks(ActionEvent event) {
        try {
            campaign.setData("clicks", Database.database.getClicksOverTime(), false);
            renderChart();
        } catch (SQLException e) {
            ExceptionDialog dialog = new ExceptionDialog(
                    "Click Load Error",
                    "Failed to load clicks.",
                    e
            );
            dialog.showAndWait();
        }
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
        try {
            campaign.setData("clickThrough", Database.database.getClickThrough(), true);

            CategoryAxis xAxis = new CategoryAxis();
            xAxis.setLabel("Time");
            xAxis.setAutoRanging(true);

            NumberAxis yAxis = new NumberAxis();
            yAxis.setLabel("Click Through Rate (%age)");

            LineChart<String, Number> chart = new LineChart<>(xAxis, yAxis);
            chart.setTitle("Average Click Through Rate per Hour");


            XYChart.Series<String, Number> seriesData = new XYChart.Series<>();
            seriesData.setName("Click Through");

            Campaign.AxisPair axisPair = campaign.data.get(Campaign.Interval.SECONDS);
            for(int i = 0; i < axisPair.getXAxis().size(); i++){
                seriesData.getData().add(new XYChart.Data<>(axisPair.getXAxis().get(i), axisPair.getYAxis().get(i).doubleValue() * 100d));
            }

            ObservableList<XYChart.Series<String, Number>> data = FXCollections.observableArrayList();
            data.add(seriesData);
            chart.setData(data);

            chartPane.setCenter(chart);
        } catch (SQLException e) {
            ExceptionDialog dialog = new ExceptionDialog(
                    "Click Load Error",
                    "Failed to load clickthrough.",
                    e
            );
            dialog.showAndWait();
        }
    }
    //</editor-fold>

    @FXML
    public void initialize() {
        if (campaign != null) campaignName.setText(campaign.getName());
        if (scene != null)
            scene.getWindow().setOnCloseRequest(e -> {
                ConfirmationDialog confirm = new ConfirmationDialog(
                        Alert.AlertType.CONFIRMATION,
                        "Exit Ad Dashboard?",
                        "Are you sure you want to exit " + campaign.getName() + " Dashboard?",
                        "Exit " + campaign.getName());
                Optional<ButtonType> result = confirm.showAndWait();
                if (result.isPresent() && confirm.isAction(result.get())) {
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
        if (this.campaign == null) this.campaign = campaign;
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    private void renderChart(){
        Campaign.AxisPair axis = campaign.data.get(Campaign.Interval.SECONDS);
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Time");
        xAxis.setAutoRanging(true);

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Clicks per Hour");

        LineChart<String, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("Clicks per Hour");


         XYChart.Series<String, Number> seriesData = new XYChart.Series<>();
         seriesData.setName("Clicks");

        Campaign.AxisPair axisPair = campaign.data.get(Campaign.Interval.SECONDS);
         for(int i = 0; i < axisPair.getXAxis().size(); i++){
             seriesData.getData().add(new XYChart.Data<>(axisPair.getXAxis().get(i), axisPair.getYAxis().get(i)));
         }

        ObservableList<XYChart.Series<String, Number>> data = FXCollections.observableArrayList();
        data.add(seriesData);
         chart.setData(data);

         chartPane.setCenter(chart);
    }
}
