package t16.controller;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import t16.AdDashboard;
import t16.components.dialogs.ConfirmationDialog;
import t16.components.dialogs.ExceptionDialog;
import t16.model.Campaign;
import t16.model.Chart;
import t16.model.Query;
import t16.model.Query.RANGE;
import t16.model.Query.TYPE;

import java.sql.Timestamp;
import java.text.NumberFormat;
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
    private TYPE currentChart = null;

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
    private StackPane mainPane;

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

    @FXML
    private Button updateButton;

    @FXML
    private BorderPane filterPanel;

    @FXML
    private StatsController statsPanel;

    @FXML
    private ProgressIndicator workingIndicator;
    //</editor-fold>

    //<editor-fold desc="View Methods">
    @FXML
    private void viewStats(ActionEvent event) {
        displayLoading(true);
        displayStats();
    }

    @FXML
    private void viewClicks(ActionEvent event) {
        displayChart(TYPE.CLICKS);
    }

    @FXML
    private void viewImpressions(ActionEvent event) {
        displayChart(TYPE.IMPRESSIONS);
    }

    @FXML
    private void viewUnique(ActionEvent event) {
        displayChart(TYPE.UNIQUES);
    }


    @FXML
    private void viewBounces(ActionEvent event) {
        displayChart(TYPE.BOUNCES);
    }

    @FXML
    private void viewConversion(ActionEvent event) {
        displayChart(TYPE.CONVERSIONS);
    }

    @FXML
    private void viewClickThrough(ActionEvent event) {
        displayChart(TYPE.CLICK_THROUGH_RATE);
    }

    @FXML
    private void updateChart(ActionEvent event) {
        displayChart(currentChart);
    }

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
                } else {
                    e.consume();
                }
            });
        if (statsPanel != null) statsPanel.setCampaign(campaign);
    }
    //</editor-fold>

    private void displayLoading(boolean working) {
        workingIndicator.setVisible(working);
        statsPanel.setVisible(false);
        mainPane.getChildren().removeIf(node -> !((node instanceof StatsController) || (node instanceof ProgressIndicator)));
    }

    private void displayStats() {
        displayLoading(false);
        filterPanel.setVisible(false);
        statsPanel.setVisible(true);
    }
    private RANGE getRange() {
        if (hourlyButton.isSelected()) {
            return RANGE.HOURLY;
        }
        if (dailyButton.isSelected()) {
            return RANGE.DAILY;
        }
        if (monthlyButton.isSelected()) {
            return RANGE.MONTHLY;
        }

        throw new IllegalStateException();
    }

    private void displayChart(Chart chart) {
        displayLoading(false);
        filterPanel.setVisible(true);
        mainPane.getChildren().add(0, chart.renderChart());
    }

    private void displayChart(TYPE t) {
        displayLoading(true);

        String title, xAxis, yAxis, series;
        RANGE range = getRange();
        xAxis = "Time";

        switch (t) {
            case IMPRESSIONS:
                title = "Impressions per {}";
                yAxis = "Impressions per {}";
                series = "Impressions";
                break;
            case CLICKS:
                title = "Clicks per {}";
                yAxis = "Clicks per {}";
                series = "Clicks";
                break;
            case UNIQUES:
                title = "Unique Clicks per {}";
                yAxis = "Unique Clicks per {}";
                series = "Unique Clicks";
                break;
            case BOUNCES:
                title = "Bounces per {}";
                yAxis = "Bounces per {}";
                series = "Bounces";
                break;
            case CONVERSIONS:
                title = "Conversions per {}";
                yAxis = "Conversions per {}";
                series = "Conversions";
                break;
            case COST:
                title = "Cost per {}";
                yAxis = "Cost per {}";
                series = "Cost";
                break;
            case COST_PER_ACQUISITION:
                title = "Cost per Acquisition per {}";
                yAxis = "Cost per Acquisition per {}";
                series = "Cost per Acquisition";
                break;
            case COST_PER_CLICK:
                title = "Cost per Click per {}";
                yAxis = "Cost per Click per {}";
                series = "Cost per Click";
                break;
            case COST_PER_1KIMPRESSION:
                title = "Cost per 1k Impressions per {}";
                yAxis = "Cost per 1k Impressions per {}";
                series = "Cost per 1k Impressions";
                break;
            case CLICK_THROUGH_RATE:
                title = "Click Through Rate per {}";
                yAxis = "Click Through Rate per {}";
                series = "Click Through Rate";
                break;
            case BOUNCE_RATE:
                title = "Bounce Rate per {}";
                yAxis = "Bounce Rate per {}";
                series = "Bounce Rate";
                break;
            default:
                return;
        }

        title = title.replace("{}", range.toString());
        yAxis = yAxis.replace("{}", range.toString());

        final String fTitle = title;
        final String fyAxis = yAxis;
        final String fxAxis = xAxis;
        final String fSeries = series;
        Task<Chart> getClicksTask = new Task<Chart>() {
            @Override
            protected Chart call() throws Exception {
                long time = System.currentTimeMillis();
                Chart c = new Chart(fTitle, fxAxis, fyAxis);

                Timestamp from = (startDate.getValue() == null) ? null : Timestamp.valueOf(startDate.getValue().atStartOfDay());
                Timestamp to = (endDate.getValue() == null) ? null : Timestamp.valueOf(endDate.getValue().atStartOfDay());
                Query query = new Query(t, range, from, to);
                c.addSeries(fSeries, AdDashboard.getDataController().getQuery(query));
                time = System.currentTimeMillis() - time;
                log.info("Chart processed in {}", NumberFormat.getNumberInstance().format(time/1000d));
                return c;
            }
        };

        getClicksTask.setOnSucceeded(e -> {
            currentChart = t;
            displayChart((Chart) e.getSource().getValue());
        });
        getClicksTask.setOnFailed(e -> {
            displayLoading(false);
            ExceptionDialog dialog = new ExceptionDialog(
                    "Click Load Error",
                    "Failed end load clicks.",
                    e.getSource().getException()
            );
            dialog.showAndWait();
        });
        AdDashboard.getWorkerPool().queueTask(getClicksTask);
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
}
