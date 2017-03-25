package t16.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Controller for Statistics View
 *
 * @author Huw Jones
 * @since 14/03/2017
 */
public class StatsControl extends VBox {

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

    private NumberFormat nf = NumberFormat.getNumberInstance(Locale.getDefault());
    private NumberFormat cf = NumberFormat.getCurrencyInstance(Locale.UK);
    private NumberFormat pf = NumberFormat.getPercentInstance(Locale.getDefault());

    public StatsControl() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/StatsControl.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public void setNumberImpressions(long numberImpressions) {
        this.numberImpressions.setText(nf.format(numberImpressions));
    }

    public void setNumberClicks(long numberClicks) {
        this.numberClicks.setText(nf.format(numberClicks));
    }

    public void setNumberUniques(long numberUniques) {
        this.numberUniques.setText(nf.format(numberUniques));
    }

    public void setNumberConversions(long numberConversions) {
        this.numberConversions.setText(nf.format(numberConversions));
    }

    public void setNumberBounces(long numberBounces) {
        this.numberBounces.setText(nf.format(numberBounces));
    }

    public void setTotalCost(BigDecimal totalCost) {
        if (totalCost == null) totalCost = BigDecimal.ZERO;
        this.totalCost.setText(cf.format(totalCost));
    }

    public void setCostPerClick(BigDecimal costPerClick) {
        if (costPerClick == null) costPerClick = BigDecimal.ZERO;
        this.costPerClick.setText(cf.format(costPerClick));
    }

    public void setCostPerAcquisition(BigDecimal costPerAcquisition) {
        if (costPerAcquisition == null) costPerAcquisition = BigDecimal.ZERO;
        this.costPerAcquisition.setText(cf.format(costPerAcquisition));
    }

    public void setCostPer1kImpressions(BigDecimal costPer1kImpressions) {
        if (costPer1kImpressions == null) costPer1kImpressions = BigDecimal.ZERO;
        this.costPer1kImpressions.setText(cf.format(costPer1kImpressions));
    }

    public void setClickThroughRate(double clickThroughRate) {
        this.clickThroughRate.setText(pf.format(clickThroughRate));
    }

    public void setBounceRate(double bounceRate) {
        this.bounceRate.setText(pf.format(bounceRate));
    }
}
