package t16.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.util.Pair;

import java.util.HashMap;
import java.util.List;

/**
 * {DESCRIPTION}
 *
 * @author Huw Jones
 * @since 07/03/2017
 */
public class Chart {

    private String title;
    private String xAxisLabel;
    private String yAxisLabel;

    ObservableList<XYChart.Series<String, Number>> seriesData = FXCollections.observableArrayList();

    HashMap<String, XYChart.Series<String, Number>> series = new HashMap<>();

    public Chart(String title, String xAxisLabel, String yAxisLabel) {
        this.title = title;
        this.xAxisLabel = xAxisLabel;
        this.yAxisLabel = yAxisLabel;
    }

    public void addSeries(String seriesName, List<Pair<String, Number>> data) {
        Series s= new Series(seriesName, data);
        series.put(seriesName, s.getSeries());
        seriesData.add(s.getSeries());
    }

    public LineChart<String, Number> renderChart() {
        LineChart<String, Number> chart = new LineChart<String, Number>(renderX(), renderY());
        chart.setTitle(title);
        chart.setData(seriesData);
        return chart;
    }

    private CategoryAxis renderX() {
        CategoryAxis axis = new CategoryAxis();
        axis.setLabel(xAxisLabel);
        axis.setAutoRanging(true);

        return axis;
    }

    private NumberAxis renderY() {
        NumberAxis axis = new NumberAxis();
        axis.setLabel(yAxisLabel);
        axis.setAutoRanging(true);

        return axis;
    }


    private class Series {

        private XYChart.Series<String, Number> series;

        public Series(String seriesName, List<Pair<String, Number>> data) {
            series = new XYChart.Series<>();
            series.setName(seriesName);
            for (Pair<String, Number> p : data) {
                series.getData().add(new XYChart.Data<>(p.getKey(), p.getValue()));
            }
        }

        public XYChart.Series<String, Number> getSeries() {
            return series;
        }
    }


}
