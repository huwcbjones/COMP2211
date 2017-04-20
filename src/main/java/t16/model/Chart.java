package t16.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
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
        //It is recommended that the true is replaced with some customisation variable
        Series s= new Series(seriesName, data, true);
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

        /**
         * If useExactNodes is false, the graph produced will be the same as before.
         * If true, each node in the graph will be an ExactDataNode (see below).
         * This does instantiate an ExactDataNode for every data point in the chart so performance might take a hit.
         * However, supervisor has mentioned before that they want something along the lines of this.
         * I recommend that there be an option somewhere in the application to toggle useExactNodes.
         */
        public Series(String seriesName, List<Pair<String, Number>> data, boolean useExactNodes) {
            series = new XYChart.Series<>();
            series.setName(seriesName);
            if(useExactNodes)
            {
                for (Pair<String, Number> p : data)
                {
                    String s = p.getKey();
                    Number n = p.getValue();
                    XYChart.Data element = new XYChart.Data<>(s, n);
                    element.setNode(new Chart.ExactDataNode(s, n));
                    series.getData().add(element);
                }
            }
            else
            {
                for (Pair<String, Number> p : data)
                {
                    series.getData().add(new XYChart.Data<>(p.getKey(), p.getValue()));
                }
            }
        }

        public XYChart.Series<String, Number> getSeries() {
            return series;
        }
    }

    /**
     * Rolling over an ExactDataNode displays its y value. Rolling off makes the value disappear.
     * Clicking the node "locks" the value onto the screen.
     * Clicking it again will "unlock" it.
     */
    public class ExactDataNode extends StackPane
    {

        public ExactDataNode(String date, Number value)
        {
            super();
            this.setPrefSize(10, 10);
            String valueString = value.toString();
            final Label numLabel = this.createLabel(valueString);
            final Label numDateLabel = this.createLabel("\t "+valueString+"\n"+date);
            this.selected = 0;
            this.setOnMouseEntered(new EventHandler<MouseEvent>()
            {
                @Override
                public void handle(MouseEvent mouseEvent)
                {
                    if(selected == 0)
                    {
                        getChildren().setAll(numLabel);
                        toFront();
                    }
                }
            });

            this.setOnMouseExited(new EventHandler<MouseEvent>()
            {
                @Override
                public void handle(MouseEvent mouseEvent)
                {
                    if(selected == 0)
                    {
                        getChildren().clear();
                    }
                }
            });

            this.setOnMouseClicked(new EventHandler<MouseEvent>()
            {
                @Override
                public void handle(MouseEvent event)
                {
                    if(selected == 0)
                    {
                        getChildren().setAll(numLabel);
                        selected = 1;
                    }
                    else if(selected == 1)
                    {
                        getChildren().setAll(numDateLabel);
                        selected = 2;
                    }
                    else if(selected == 2)
                    {
                        getChildren().clear();
                        selected = 0;
                    }
                }
            });
        }

        private Label createLabel(String value) {
            final Label label = new Label(value);
            label.setAlignment(Pos.TOP_CENTER);
            label.setStyle("-fx-font-size: 10; -fx-font-weight: bold;");
            label.setTextFill(Color.DARKBLUE);
            label.setMinSize(Label.USE_PREF_SIZE, Label.USE_PREF_SIZE);
            return label;
        }
    }
}
