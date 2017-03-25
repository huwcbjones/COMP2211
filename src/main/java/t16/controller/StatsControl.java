package t16.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import t16.model.Campaign;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * {DESCRIPTION}
 *
 * @author Huw Jones
 * @since 14/03/2017
 */
public class StatsControl extends GridPane {

    @FXML
    private Label numberImpressions;

    @FXML
    private Label numberClicks;

    @FXML
    private Label numberUniques;

    @FXML
    private Label numberConversions;

    @FXML
    private Label numberBounces;

    @FXML
    private Label totalCost;

    @FXML
    private Label costPerClick;

    @FXML
    private Label costPerAcquisition;

    @FXML
    private Label costPer1kImpressions;

    @FXML
    private Label clickThroughRate;

    @FXML
    private Label bounceRate;

    private NumberFormat nf = NumberFormat.getNumberInstance(Locale.getDefault());
    private NumberFormat cf = NumberFormat.getCurrencyInstance(Locale.UK);

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

    public void setCampaign(Campaign c) {
        if (c == null) return;
        if (numberImpressions != null) numberImpressions.setText(nf.format(c.getNumberImpressions()));
        if (numberClicks != null) numberClicks.setText(nf.format(c.getNumberClicks()));
        if (numberUniques != null) numberUniques.setText(nf.format(c.getNumberUniques()));
        if (numberConversions != null) numberConversions.setText(nf.format(c.getNumberConversions()));
        if (numberBounces != null) numberBounces.setText(nf.format(Dashboard.BOUNCE_DEFINITION ? c.getNumberBouncesPages() : c.getNumberBouncesTime()));

        if (totalCost != null) totalCost.setText(cf.format(c.getTotalCost()));
        if (costPerClick != null) costPerClick.setText(cf.format(c.getCostPerClick()));
        if (costPerAcquisition != null) {
            if(c.getCostPerAcquisition() != null) costPerAcquisition.setText(cf.format(c.getCostPerAcquisition()));
        }
        if (costPer1kImpressions != null) costPer1kImpressions.setText(cf.format(c.getCostPer1kImpressions()));


        if (clickThroughRate != null) clickThroughRate.setText(nf.format(c.getClickThroughRate()) + "%");
        if (bounceRate != null) bounceRate.setText(nf.format(Dashboard.BOUNCE_DEFINITION ? c.getBounceRatePages() : c.getBounceRateTime()) + "%");
    }
}
