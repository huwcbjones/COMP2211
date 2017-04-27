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
import t16.model.Query.TYPE;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

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
    private StackPane mainPane;

    @FXML
    private BorderPane filterPanel;

    @FXML
    private StatsControl statsPanel;

    @FXML
    private FilterControl filterController;

    @FXML
    private ProgressIndicator workingIndicator;

    @FXML
    private Button clicksButton;

    @FXML
    private MenuItem saveChart;

    @FXML
    private MenuItem printChart;


    //</editor-fold>

    //<editor-fold desc="View Methods">

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
        renderChart(TYPE.BOUNCES);
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
        renderChart(TYPE.BOUNCE_RATE);
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
        mainPane.getChildren().removeIf(node -> !(node instanceof ProgressIndicator));
    }

    /**
     * Displays a Chart on the View
     *
     * @param chart
     */
    private void displayChart(Chart chart) {
        displayLoading(false);
        mainPane.getChildren().add(0, chart.renderChart());
    }

    /**
     * Renders a Chart, then displays it
     *
     * @param t Chart Type to render
     */
    private void renderChart(final TYPE t) {
        if (t == null) return;
        displayLoading(true);

        String title;
        String xAxis;
        String yAxis;
        xAxis = "Time";
        switch (t) {
            case IMPRESSIONS:
                title = "Impressions per {}";
                yAxis = "Impressions per {}";
                break;
            case CLICKS:
                title = "Clicks per {}";
                yAxis = "Clicks per {}";
                break;
            case UNIQUES:
                title = "Unique Clicks per {}";
                yAxis = "Unique Clicks per {}";
                break;
            case BOUNCES:
                title = "Bounces per {}";
                yAxis = "Bounces per {}";
                break;
            case CONVERSIONS:
                title = "Conversions per {}";
                yAxis = "Conversions per {}";
                break;
            case TOTAL_COST:
                title = "Total Cost per {}";
                yAxis = "Total Cost per {}";
                break;
            case COST_PER_ACQUISITION:
                title = "Cost per Acquisition per {}";
                yAxis = "Cost per Acquisition per {}";
                break;
            case COST_PER_CLICK:
                title = "Cost per Click per {}";
                yAxis = "Cost per Click per {}";
                break;
            case COST_PER_THOUSAND_IMPRESSIONS:
                title = "Cost per 1k Impressions per {}";
                yAxis = "Cost per 1k Impressions per {}";
                break;
            case CLICK_THROUGH_RATE:
                title = "Click Through Rate per {}";
                yAxis = "Click Through Rate per {}";
                break;
            case BOUNCE_RATE:
                title = "Bounce Rate per {}";
                yAxis = "Bounce Rate per {}";
                break;
            default:
                log.warn("No handler for chart type {}", t.toString());
                displayLoading(false);
                return;
        }

        title = title.replace("{}", filterController.getRangeString());
        yAxis = yAxis.replace("{}", filterController.getRangeString());

        final String fTitle = title;
        final String fyAxis = yAxis;
        final String fxAxis = xAxis;
        Task<Chart> processChartTask = new Task<Chart>()
        {
            @Override
            protected Chart call() throws Exception {
                long time = System.currentTimeMillis();
                final Chart c = new Chart(fTitle, fxAxis, fyAxis);
                for(IndividualFilter iF : filterController.getIndividualFilters())
                {
                    Query query = filterController.getQuery(t, iF);
                    log.debug("Query: {}", query.getQuery());
                    c.addSeries(iF.toString(), AdDashboard.getDataController().getQuery(query));
                }
                time = System.currentTimeMillis() - time;
                log.info("Chart processed in {}", NumberFormat.getNumberInstance().format(time / 1000d));
                return c;
            }
        };

        processChartTask.setOnSucceeded(e -> {
            currentChart = t;
            displayChart((Chart) e.getSource().getValue());
        });
        processChartTask.setOnFailed(e -> {
            displayLoading(false);
            ExceptionDialog dialog = new ExceptionDialog(
                    "Chart Load Error",
                    "Failed to load chart. " + e.getSource().getMessage(),
                    e.getSource().getException()
            );
            dialog.showAndWait();
        });
        AdDashboard.getWorkerPool().queueTask(processChartTask);
    }
    //</editor-fold>

    @FXML
    /**
     * Initialises the View
     */
    public void initialize() {
        filterController.addUpdateListener(e -> renderChart(currentChart));
        saveChart.setOnAction(e -> Export.saveChart(this.scene.getWindow(), mainPane));
        printChart.setOnAction(e -> Export.printChart(this.scene.getWindow(), mainPane));

        log.info("Dashboard initialised!");
        loadStats();
    }

    public void loadStats()
    {
        log.info("Loading statistics...");
        Task<Long> statsTask = new Task<Long>() {
            @Override
            protected Long call() throws Exception
            {
                long startTime = System.currentTimeMillis();

                ArrayList<Task> taskList = new ArrayList<>();

                //<editor-fold desc="Create tasks">
                Task<Long> impressionsTask = new Task<Long>() {
                    @Override
                    protected Long call() throws Exception {
                        return AdDashboard.getDataController().getTotalImpressions();
                    }
                };
                taskList.add(impressionsTask);

                Task<Long> clicksTask = new Task<Long>() {
                    @Override
                    protected Long call() throws Exception {
                        return AdDashboard.getDataController().getTotalClicks();
                    }
                };
                taskList.add(clicksTask);

                Task<Long> uniquesTask = new Task<Long>() {
                    @Override
                    protected Long call() throws Exception {
                        return AdDashboard.getDataController().getTotalUniques();
                    }
                };
                taskList.add(uniquesTask);

                Task<Long> conversionsTask = new Task<Long>() {
                    @Override
                    protected Long call() throws Exception {
                        return AdDashboard.getDataController().getTotalConversions();
                    }
                };
                taskList.add(conversionsTask);

                Task<Long> bouncesPagesTask = new Task<Long>() {
                    @Override
                    protected Long call() throws Exception {
                        return AdDashboard.getDataController().getTotalBouncesPages();
                    }
                };
                taskList.add(bouncesPagesTask);

                Task<Long> bouncesTimeTask = new Task<Long>() {
                    @Override
                    protected Long call() throws Exception {
                        return AdDashboard.getDataController().getTotalBouncesTime();
                    }
                };
                taskList.add(bouncesTimeTask);

                Task<BigDecimal> totalCostTask = new Task<BigDecimal>() {
                    @Override
                    protected BigDecimal call() throws Exception {
                        return AdDashboard.getDataController().getTotalCost();
                    }
                };
                taskList.add(totalCostTask);

                Task<BigDecimal> costPerClickTask = new Task<BigDecimal>() {
                    @Override
                    protected BigDecimal call() throws Exception {
                        return AdDashboard.getDataController().getCostPerClick();
                    }
                };
                taskList.add(costPerClickTask);

                Task<BigDecimal> costPerAcquisitionTask = new Task<BigDecimal>() {
                    @Override
                    protected BigDecimal call() throws Exception {
                        return AdDashboard.getDataController().getCostPerAcquisition();
                    }
                };
                taskList.add(costPerAcquisitionTask);

                Task<BigDecimal> costPer1kTask = new Task<BigDecimal>() {
                    @Override
                    protected BigDecimal call() throws Exception {
                        return AdDashboard.getDataController().getCostPer1kImpressions();
                    }
                };
                taskList.add(costPer1kTask);

                Task<Double> clickThruTask = new Task<Double>() {
                    @Override
                    protected Double call() throws Exception {
                        return AdDashboard.getDataController().getClickThroughRate();
                    }
                };
                taskList.add(clickThruTask);

                Task<Double> bounceRatePagesTask = new Task<Double>() {
                    @Override
                    protected Double call() throws Exception {
                        return AdDashboard.getDataController().getBounceRatePages();
                    }
                };
                taskList.add(bounceRatePagesTask);

                Task<Double> bounceRateTimeTask = new Task<Double>() {
                    @Override
                    protected Double call() throws Exception {
                        return AdDashboard.getDataController().getBounceRateTime();
                    }
                };
                taskList.add(bounceRateTimeTask);
                //</editor-fold>

                CountDownLatch latch = new CountDownLatch(taskList.size());

                //<editor-fold desc="Add Success Handlers">
                impressionsTask.setOnSucceeded(event -> {
                    statsPanel.setNumberImpressions((long) event.getSource().getValue());
                    latch.countDown();
                    log.info("Impressions loaded");
                });
                clicksTask.setOnSucceeded(event -> {
                    statsPanel.setNumberClicks((long) event.getSource().getValue());
                    latch.countDown();
                    log.info("Clicks loaded");
                });
                uniquesTask.setOnSucceeded(event -> {
                    statsPanel.setNumberUniques((long) event.getSource().getValue());
                    latch.countDown();
                    log.info("Uniques loaded");
                });
                conversionsTask.setOnSucceeded(event -> {
                    statsPanel.setNumberConversions((long) event.getSource().getValue());
                    latch.countDown();
                    log.info("Conversions loaded");
                });
                bouncesPagesTask.setOnSucceeded(event -> {
                    statsPanel.setNumberPageBounces((long) event.getSource().getValue());
                    latch.countDown();
                    log.info("Page Bounces loaded");
                });
                bouncesTimeTask.setOnSucceeded(event -> {
                    statsPanel.setNumberTimeBounces((long) event.getSource().getValue());
                    latch.countDown();
                    log.info("Time Bounces loaded");
                });
                totalCostTask.setOnSucceeded(event -> {
                    statsPanel.setTotalCost((BigDecimal) event.getSource().getValue());
                    latch.countDown();
                    log.info("Total Cost loaded");
                });
                costPerClickTask.setOnSucceeded(event -> {
                    statsPanel.setCostPerClick((BigDecimal) event.getSource().getValue());
                    latch.countDown();
                    log.info("Cost per Click loaded");
                });
                costPerAcquisitionTask.setOnSucceeded(event -> {
                    statsPanel.setCostPerAcquisition((BigDecimal) event.getSource().getValue());
                    latch.countDown();
                    log.info("Cost per Acquisition loaded");
                });
                costPer1kTask.setOnSucceeded(event -> {
                    statsPanel.setCostPer1kImpressions((BigDecimal) event.getSource().getValue());
                    latch.countDown();
                    log.info("Cost per 1k Impressions loaded");
                });
                clickThruTask.setOnSucceeded(event -> {
                    statsPanel.setClickThroughRate((double) event.getSource().getValue());
                    latch.countDown();
                    log.info("Click Through Rate loaded");
                });
                bounceRatePagesTask.setOnSucceeded(event -> {
                    statsPanel.setBounceRatePages((double) event.getSource().getValue());
                    latch.countDown();
                    log.info("Bounce Rate Pages loaded");
                });
                bounceRateTimeTask.setOnSucceeded(event -> {
                    statsPanel.setBounceRateTime((double) event.getSource().getValue());
                    latch.countDown();
                    log.info("Bounce Rate Time loaded");
                });
                //</editor-fold>

                taskList.forEach(e -> AdDashboard.getWorkerPool().queueTask(e));
                latch.await(30, TimeUnit.SECONDS);
                return (System.currentTimeMillis() - startTime);
            }
        };

        statsTask.setOnSucceeded(e -> log.info("Statistics loaded in {}ms", e.getSource().getValue()));
        AdDashboard.getWorkerPool().queueTask(new Task() {
            @Override
            protected Long call() throws Exception {
                Dashboard.this.clicksButton.fire();
                return null;
            }
        });
        AdDashboard.getWorkerPool().queueTask(statsTask);
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
        addExitHandler();
    }

    protected void addExitHandler() {
        this.scene.getWindow().setOnCloseRequest(e -> {
            ConfirmationDialog confirm = new ConfirmationDialog(
                    Alert.AlertType.CONFIRMATION,
                    "Exit Ad Dashboard?",
                    "Are you sure you want to exit the " + campaign.getName() + " Dashboard?",
                    "Exit " + campaign.getName());
            Optional<ButtonType> result = confirm.showAndWait();
            if (result.isPresent() && confirm.isAction(result.get())) {
                Platform.exit();
            } else {
                e.consume();
            }
        });
    }
}
