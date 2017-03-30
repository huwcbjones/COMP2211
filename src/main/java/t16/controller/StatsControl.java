package t16.controller;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;

/**
 * Controller for Statistics View
 *
 * @author Huw Jones
 * @since 14/03/2017
 */
public class StatsControl extends VBox {

    private static NumberFormat nf = NumberFormat.getNumberInstance(Locale.getDefault());
    private static NumberFormat cf = NumberFormat.getCurrencyInstance(Locale.UK);
    private static NumberFormat pf = NumberFormat.getPercentInstance(Locale.getDefault());

    static {
        pf.setMinimumFractionDigits(3);
        cf.setMinimumFractionDigits(3);
    }

    //<editor-fold desc="View Controls">
    @FXML
    private Label impressionsLabel;
    @FXML
    private Label numberImpressions;
    @FXML
    private Label clicksLabel;
    @FXML
    private Label numberClicks;
    @FXML
    private Label uniquesLabel;
    @FXML
    private Label numberUniques;
    @FXML
    private Label conversionsLabel;
    @FXML
    private Label numberConversions;
    @FXML
    private Label bouncesLabel;
    @FXML
    private Label numberBounces;
    @FXML
    private Label totalCostLabel;
    @FXML
    private Label totalCost;
    @FXML
    private Label costClickLabel;
    @FXML
    private Label costPerClick;
    @FXML
    private Label costAquisitionLabel;
    @FXML
    private Label costPerAcquisition;
    @FXML
    private Label cost1kImpressionLabel;
    @FXML
    private Label costPer1kImpressions;
    @FXML
    private Label clickThruLabel;
    @FXML
    private Label clickThroughRate;
    @FXML
    private Label bounceRateLabel;
    @FXML
    private Label bounceRate;
    //</editor-fold>

    private HashMap<Label, Timeline> timelines = new HashMap<>();

    public StatsControl() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/StatsControl.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        createTimeline();
    }

    public void setNumberImpressions(long numberImpressions) {
        stopLabel(this.numberImpressions);
        this.numberImpressions.setText(nf.format(numberImpressions));
    }

    public void setNumberClicks(long numberClicks) {
        stopLabel(this.numberClicks);
        this.numberClicks.setText(nf.format(numberClicks));
    }

    public void setNumberUniques(long numberUniques) {
        stopLabel(this.numberUniques);
        this.numberUniques.setText(nf.format(numberUniques));
    }

    public void setNumberConversions(long numberConversions) {
        stopLabel(this.numberConversions);
        this.numberConversions.setText(nf.format(numberConversions));
    }

    public void setNumberBounces(long numberBounces) {
        stopLabel(this.numberBounces);
        this.numberBounces.setText(nf.format(numberBounces));
    }

    public void setTotalCost(BigDecimal totalCost) {
        stopLabel(this.totalCost);
        if (totalCost == null) totalCost = BigDecimal.ZERO;
        this.totalCost.setText(cf.format(totalCost));
    }

    public void setCostPerClick(BigDecimal costPerClick) {
        stopLabel(this.costPerClick);
        if (costPerClick == null) costPerClick = BigDecimal.ZERO;
        this.costPerClick.setText(cf.format(costPerClick));
    }

    public void setCostPerAcquisition(BigDecimal costPerAcquisition) {
        stopLabel(this.costPerAcquisition);
        if (costPerAcquisition == null) costPerAcquisition = BigDecimal.ZERO;
        this.costPerAcquisition.setText(cf.format(costPerAcquisition));
    }

    public void setCostPer1kImpressions(BigDecimal costPer1kImpressions) {
        stopLabel(this.costPer1kImpressions);
        if (costPer1kImpressions == null) costPer1kImpressions = BigDecimal.ZERO;
        this.costPer1kImpressions.setText(cf.format(costPer1kImpressions));
    }

    public void setClickThroughRate(double clickThroughRate) {
        stopLabel(this.clickThroughRate);
        this.clickThroughRate.setText(pf.format(clickThroughRate));
    }

    public void setBounceRate(double bounceRate) {
        stopLabel(this.bounceRate);
        this.bounceRate.setText(pf.format(bounceRate));
    }

    protected void createTimeline() {
        ArrayList<Label> labels = new ArrayList<>(Arrays.asList(new Label[]{
                numberImpressions,
                numberClicks,
                numberUniques,
                numberConversions,
                numberBounces,
                totalCost,
                costPerClick,
                costPerAcquisition,
                costPer1kImpressions,
                clickThroughRate,
                bounceRate
        }));

        for (Label l : labels) {
            Timeline t = new Timeline();
            String str = "";
            t.getKeyFrames().add(new KeyFrame(new Duration(0), new KeyValue(l.textProperty(), str)));
            for(int i = 1; i <= 3; i++){
                str += ".";
                t.getKeyFrames().add(new KeyFrame(new Duration(i * 300), new KeyValue(l.textProperty(), str)));
            }
            t.getKeyFrames().add(new KeyFrame(new Duration(1200), new KeyValue(l.textProperty(), str)));
            t.setCycleCount(Animation.INDEFINITE);
            timelines.put(l, t);
        }

        timelines.forEach((label, timeline) -> timeline.play());
    }

    private void stopLabel(Label l){
        timelines.get(l).stop();
    }

}
