package t16.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import t16.AdDashboard;
import t16.components.dialogs.ConfirmationDialog;
import t16.components.dialogs.ExceptionDialog;
import t16.model.Campaign;
import t16.model.Chart;
import t16.model.Database;
import t16.controller.DataController;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Optional;

/**
 * Dashboard Controller
 *
 * @author Huw Jones
 * @since 25/02/2017
 */
public class Dashboard {
    protected static final Logger log = LogManager.getLogger(Dashboard.class);

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

    @FXML
    private ToggleButton hourlyButton;

    @FXML
    private ToggleButton dailyButton;

    @FXML
    private ToggleButton weeklyButton;

    @FXML
    private ToggleButton monthlyButton;

    @FXML
    private ToggleGroup rangeToggle;
    //</editor-fold>

    //<editor-fold desc="View Methods">
    @FXML
    private void viewClicks(ActionEvent event) {
        Task<Chart> getClicksTask = new Task<Chart>() {
            @Override
            protected Chart call() throws Exception {
                Chart c = new Chart("Clicks per Hour", "Time", "Clicks per Hour");

                Timestamp from = (startDate.getValue() == null) ? null : Timestamp.valueOf(startDate.getValue().atStartOfDay());
                Timestamp to = (endDate.getValue() == null) ? null : Timestamp.valueOf(endDate.getValue().atStartOfDay());
                //TODO Change when the appropriate GUI filter options are added
                String gender = "Male"; //Or "Female" or "n/a"
                String age = "<25"; //Or "25-34" or "n/a" or whatever
                String income = "Low"; //Or "Medium" or "High" or "n/a"
                String context = "Shopping"; //Or "Business" or "n/a" or god knows what

                c.addSeries("Clicks", AdDashboard.getDataController().getClicks(Dashboard.this.getRange(), from, to, gender, age, income, context));
                return c;
            }
        };

        getClicksTask.setOnSucceeded(e -> {
            chartPane.setCenter(((Chart) e.getSource().getValue()).renderChart());
        });
        getClicksTask.setOnFailed(e -> {
            ExceptionDialog dialog = new ExceptionDialog(
                    "Click Load Error",
                    "Failed end load clicks.",
                    e.getSource().getException()
            );
            dialog.showAndWait();
        });
        AdDashboard.getWorkerPool().queueTask(getClicksTask);
        //        displayChart(TYPE.CLICKS);

    }

    @FXML
    private void viewImpressions(ActionEvent event) {
//        displayChart(TYPE.IMPRESSIONS);

    }

    @FXML
    private void viewUnique(ActionEvent event) {
//        displayChart(TYPE.UNIQUES);

    }

    @FXML
    private void viewBounces(ActionEvent event) {
//        displayChart(TYPE.BOUNCES);

    }

    @FXML
    private void viewConversion(ActionEvent event) {
//        displayChart(TYPE.CONVERSIONS);
    }

    @FXML
    private void viewTotalCost(ActionEvent event) {
//        displayChart(TYPE.CONVERSIONS);

    }

    @FXML
    private void viewCostPerAcquisition(ActionEvent event) {
//        displayChart(TYPE.COST_PER_ACQUISITION);

    }

    @FXML
    private void viewCostPerClick(ActionEvent event) {
//        displayChart(TYPE.COST_PER_CLICK);

    }

    @FXML
    private void viewCostPerThousandImpressions(ActionEvent event) {
//        displayChart(TYPE.COST_PER_THOUSAND_IMPRESSIONS);

    }

    @FXML
    private void viewClickCost(ActionEvent event) {
//        displayChart(TYPE.CLICK_COST);

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
            for (int i = 0; i < axisPair.getXAxis().size(); i++) {
                seriesData.getData().add(new XYChart.Data<>(axisPair.getXAxis().get(i), axisPair.getYAxis().get(i).doubleValue() * 100d));
            }

            ObservableList<XYChart.Series<String, Number>> data = FXCollections.observableArrayList();
            data.add(seriesData);
            chart.setData(data);

            chartPane.setCenter(chart);
        } catch (SQLException e) {
            log.catching(e);
            ExceptionDialog dialog = new ExceptionDialog(
                    "Click Load Error",
                    "Failed end load clickthrough.",
                    e
            );
            dialog.showAndWait();
        }

        //displayChart(TYPE.CLICK_THROUGH_RATE);
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
                        "Are you sure you want end exit " + campaign.getName() + " Dashboard?",
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
     * @param campaign Campaign end view
     */
    public void setCampaign(Campaign campaign) {
        if (this.campaign == null) this.campaign = campaign;
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    public DataController.RANGE getRange(){
        if(hourlyButton.isSelected()){
            return DataController.RANGE.HOURLY;
        }
        if(dailyButton.isSelected()){
            return DataController.RANGE.DAILY;
        }
        if(weeklyButton.isSelected()){
            return DataController.RANGE.WEEKLY;
        }
        if(monthlyButton.isSelected()){
            return DataController.RANGE.MONTHLY;
        }

        throw new IllegalStateException();
    }
}
