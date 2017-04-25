package t16.controller;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import t16.events.FilterUpdateListener;
import t16.model.Query;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;


/**
 * {DESCRIPTION}
 *
 * @author Huw Jones
 * @since 25/03/2017
 */
public class FilterControl extends VBox {

    //<editor-fold desc="View Components">
    @FXML
    protected ToggleButton hourlyButton;

    @FXML
    protected ToggleButton dailyButton;

    @FXML
    protected ToggleButton monthlyButton;

    @FXML
    protected DatePicker startDate;

    @FXML
    protected DatePicker endDate;

    @FXML
    protected TabPane individualFiltersBox;

    @FXML
    protected Button newTabButton;
    //</editor-fold>

    private Query.TYPE lastQuery = null;

    private ArrayList<FilterUpdateListener> listenerList = new ArrayList<>();

    private Tab addTabTab = new Tab("+");

    public FilterControl() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/FilterControl.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        initialise();
    }

    public void initialise() {
        individualFiltersBox.getTabs().add(new Tab("1", new IndividualFilter()));
        individualFiltersBox.getTabs().add(addTabTab);
        individualFiltersBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == addTabTab) {
                addNewTab();
            }
        });

        initialiseEventListeners();
    }

    protected void initialiseEventListeners() {
        startDate.setOnAction(this::triggerUpdateEvent);
        endDate.setOnAction(this::triggerUpdateEvent);
    }

    public Query getQuery(Query.TYPE type, IndividualFilter iF) {
        lastQuery = type;
        Timestamp from = getFromDate();
        Timestamp to = getToDate();
        Query.RANGE range = getRange();

        return iF.getSpecificQuery(type, range, from, to);
    }

    public void addUpdateListener(FilterUpdateListener listener) {
        listenerList.add(listener);
        for (IndividualFilter iF : this.getIndividualFilters()) {
            if (iF != null) iF.addUpdateListener(listener);
        }
    }

    public void removeUpdateListener(FilterUpdateListener listener) {
        listenerList.remove(listener);
        for (IndividualFilter iF : this.getIndividualFilters()) {
            iF.removeUpdateListener(listener);
        }
    }

    protected void triggerUpdateEvent(ActionEvent e) {
        listenerList.iterator().forEachRemaining(l -> l.filterUpdated(e));
    }

    /**
     * Gets the range type from the range buttons
     *
     * @return Range Type (Hour, Day, Month)
     */
    public Query.RANGE getRange() {
        if (hourlyButton.isSelected()) {
            return Query.RANGE.HOUR;
        }
        if (dailyButton.isSelected()) {
            return Query.RANGE.DAY;
        }
        if (monthlyButton.isSelected()) {
            return Query.RANGE.MONTH;
        }

        throw new IllegalStateException();
    }

    public Timestamp getFromDate() {
        return (startDate.getValue() == null) ? null : Timestamp.valueOf(startDate.getValue().atStartOfDay());
    }

    public Timestamp getToDate() {
        return (endDate.getValue() == null) ? null : Timestamp.valueOf(endDate.getValue().atStartOfDay());
    }

    public String getRangeString() {
        Query.RANGE range = getRange();
        return range.toString().substring(0, 1).toUpperCase() + range.toString().substring(1).toLowerCase();
    }

    public ArrayList<IndividualFilter> getIndividualFilters() {
        ObservableList<Tab> tabs = this.individualFiltersBox.getTabs();
        ArrayList<IndividualFilter> filters = new ArrayList<>();
        Iterator<Tab> i = tabs.iterator();
        Tab t;
        while (i.hasNext()) {
            t = i.next();
            if (t.getContent() != null) filters.add((IndividualFilter) t.getContent());
        }
        return filters;
    }

    private void addNewTab() {
        ObservableList<Tab> tabs = this.individualFiltersBox.getTabs();
        //Tab names work until you start deleting earlier tabs...
        //Might be better to set the colour of the tab to the colour of the graph instead of using names,
        //especially with limited space for tab names
        IndividualFilter iF = new IndividualFilter();
        for (FilterUpdateListener ful : this.listenerList) {
            iF.addUpdateListener(ful);
        }
        Tab t = new Tab(tabs.size() + "", iF);
        tabs.add(t);
        tabs.remove(addTabTab);
        tabs.add(addTabTab);
        individualFiltersBox.getSelectionModel().select(t);
    }
}
