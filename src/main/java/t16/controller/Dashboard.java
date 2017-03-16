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
import t16.model.*;
import t16.model.Query.*;

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
    /**
     * If true, a bounce is defined as 1 page being viewed.
     * Otherwise, less than 60 seconds being spent on the site
     */
    public static boolean BOUNCE_DEFINITION;

    private Scene scene = null;
    private Campaign campaign = null;
    private TYPE currentChart = null;

    //<editor-fold desc="View Controls">
    @FXML
    private Label campaignName;

    @FXML
    private StackPane mainPane;

    @FXML
    private DatePicker startDate;

    @FXML
    private DatePicker endDate;

    @FXML
    private BorderPane filterPanel;

    @FXML
    private ToggleButton hourlyButton;

    @FXML
    private ToggleButton dailyButton;

    @FXML
    private ToggleButton monthlyButton;

    @FXML
    private ComboBox<Gender> genderCombo;

    @FXML
    private ComboBox ageCombo;

    @FXML
    private ComboBox<Income> incomeCombo;

    @FXML
    private ComboBox<Context> contextCombo;

    @FXML
    private Button bounceToggle;

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
        renderChart(TYPE.CLICKS);
    }

    @FXML
    private void viewImpressions(ActionEvent event) {
        renderChart(TYPE.IMPRESSIONS);
    }

    @FXML
    private void viewUnique(ActionEvent event) {
        renderChart(TYPE.UNIQUES);
    }

    @FXML
    private void viewBounces(ActionEvent event) {
        renderChart(BOUNCE_DEFINITION ? TYPE.BOUNCES_PAGES : TYPE.BOUNCES_TIME);
    }

    @FXML
    private void viewConversion(ActionEvent event) {
        renderChart(TYPE.CONVERSIONS);
    }

    @FXML
    private void viewTotalCost(ActionEvent event) {
        renderChart(TYPE.TOTAL_COST);
    }

    @FXML
    private void viewCostPerAcquisition(ActionEvent event) {
        renderChart(TYPE.COST_PER_ACQUISITION);
    }

    @FXML
    private void viewCostPerClick(ActionEvent event) {
        renderChart(TYPE.COST_PER_CLICK);
    }

    @FXML
    private void viewCostPerThousandImpressions(ActionEvent event) {
        renderChart(TYPE.COST_PER_THOUSAND_IMPRESSIONS);
    }

    @FXML
    private void viewClickThrough(ActionEvent event) {
        renderChart(TYPE.CLICK_THROUGH_RATE);
    }

    @FXML
    private void viewBounceRate(ActionEvent event) {
        renderChart(BOUNCE_DEFINITION ? TYPE.BOUNCE_RATE_PAGES : TYPE.BOUNCE_RATE_TIME);
    }

    @FXML
    private void updateChart(ActionEvent event) {
        renderChart(currentChart);
    }

    @FXML
    private void bounceToggleAction(ActionEvent event)
    {
        BOUNCE_DEFINITION = !BOUNCE_DEFINITION;
        bounceToggle.setText(BOUNCE_DEFINITION ? "Bounce: 1 page viewed" : "Bounce: viewed < 60s");
    }

    //</editor-fold>

    //<editor-fold desc="Helper Methods">

    /**
     * Displays the loading wheel
     *
     * @param working True if work is happening, false if work stopped
     */
    private void displayLoading(boolean working) {
        workingIndicator.setVisible(working);
        statsPanel.setVisible(false);
        mainPane.getChildren().removeIf(node -> !((node instanceof StatsController) || (node instanceof ProgressIndicator)));
    }

    /**
     * Displays the "Stats" Dashboard
     */
    private void displayStats() {
        displayLoading(false);
        filterPanel.setVisible(false);
        statsPanel.setVisible(true);
    }

    /**
     * Displays a Chart on the View
     *
     * @param chart
     */
    private void displayChart(Chart chart) {
        displayLoading(false);
        filterPanel.setVisible(true);
        mainPane.getChildren().add(0, chart.renderChart());
    }

    /**
     * Renders a Chart, then displays it
     *
     * @param t Chart Type to render
     */
    private void renderChart(TYPE t) {
        if (t == null) return;
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
            case BOUNCES_PAGES:
                title = "Bounces per {}";
                yAxis = "Bounces per {}";
                series = "Bounces";
                break;
            case BOUNCES_TIME:
                title = "Bounces per {}";
                yAxis = "Bounces per {}";
                series = "Bounces";
                break;
            case CONVERSIONS:
                title = "Conversions per {}";
                yAxis = "Conversions per {}";
                series = "Conversions";
                break;
            case TOTAL_COST:
                title = "Total Cost per {}";
                yAxis = "Total Cost per {}";
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
            case COST_PER_THOUSAND_IMPRESSIONS:
                title = "Cost per 1k Impressions per {}";
                yAxis = "Cost per 1k Impressions per {}";
                series = "Cost per 1k Impressions";
                break;
            case CLICK_THROUGH_RATE:
                title = "Click Through Rate per {}";
                yAxis = "Click Through Rate per {}";
                series = "Click Through Rate";
                break;
            case BOUNCE_RATE_PAGES:
                title = "Bounce Rate per {}";
                yAxis = "Bounce Rate per {}";
                series = "Bounce Rate";
                break;
            case BOUNCE_RATE_TIME:
                title = "Bounce Rate per {}";
                yAxis = "Bounce Rate per {}";
                series = "Bounce Rate";
                break;
            default:
                return;
        }

        title = title.replace("{}", range.toString().substring(0, 1).toUpperCase() + range.toString().substring(1).toLowerCase());
        yAxis = yAxis.replace("{}", range.toString().substring(0, 1).toUpperCase() + range.toString().substring(1).toLowerCase());

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

                GENDER gender = GENDER.ALL;
                if (genderCombo.getSelectionModel().getSelectedItem() != null) {
                    gender = genderCombo.getSelectionModel().getSelectedItem().getType();
                }

                INCOME income = INCOME.ALL;
                if (incomeCombo.getSelectionModel().getSelectedItem() != null) {
                    income = incomeCombo.getSelectionModel().getSelectedItem().getType();
                }

                // TODO: Age

                CONTEXT context = CONTEXT.ALL;
                if (contextCombo.getSelectionModel().getSelectedItem() != null) {
                    context = contextCombo.getSelectionModel().getSelectedItem().getType();
                }

                Query query = new Query(t, range, from, to, gender, null, income, context);

                c.addSeries(fSeries, AdDashboard.getDataController().getQuery(query));
                time = System.currentTimeMillis() - time;
                log.info("Chart processed in {}", NumberFormat.getNumberInstance().format(time / 1000d));
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
     * Gets the range type from the range buttons
     *
     * @return Range Type (Hour, Day, Month)
     */
    private RANGE getRange() {
        if (hourlyButton.isSelected()) {
            return RANGE.HOUR;
        }
        if (dailyButton.isSelected()) {
            return RANGE.DAY;
        }
        if (monthlyButton.isSelected()) {
            return RANGE.MONTH;
        }

        throw new IllegalStateException();
    }
    //</editor-fold>

    @FXML
    /**
     * Initialises the View
     */
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

        genderCombo.getItems().addAll(
                new Gender(GENDER.ALL, "All"),
                new Gender(GENDER.FEMALE, "Female"),
                new Gender(GENDER.MALE, "Male")
        );

        incomeCombo.getItems().addAll(
                new Income(INCOME.ALL, "All"),
                new Income(INCOME.HIGH, "High"),
                new Income(INCOME.MEDIUM, "Medium"),
                new Income(INCOME.LOW, "Low")
        );

        contextCombo.getItems().addAll(
                new Context(CONTEXT.ALL, "All"),
                new Context(CONTEXT.BLOG, "Blog"),
                new Context(CONTEXT.HOBBIES, "Hobbies"),
                new Context(CONTEXT.NEWS, "News"),
                new Context(CONTEXT.SHOPPING, "Shopping"),
                new Context(CONTEXT.SOCIAL_MEDIA, "Social Media"),
                new Context(CONTEXT.TRAVEL, "Travel")
        );
    }

    /**
     * Sets the stats view
     */
    public void setStats() {
        if (statsPanel != null) statsPanel.setCampaign(campaign);
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
