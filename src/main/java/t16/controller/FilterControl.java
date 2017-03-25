package t16.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.VBox;
import t16.events.FilterUpdateListener;
import t16.model.*;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;


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
    protected ComboBox<Gender> genderCombo;

    @FXML
    protected ComboBox<Age> ageCombo;

    @FXML
    protected ComboBox<Income> incomeCombo;

    @FXML
    protected ComboBox<Context> contextCombo;

    @FXML
    protected Button bounceToggle;
    //</editor-fold>

    private boolean isBounceTime = false;

    private Query.TYPE lastQuery = null;

    private ArrayList<FilterUpdateListener> listenerList = new ArrayList<>();

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
        genderCombo.getItems().addAll(
                new Gender(Query.GENDER.ALL, "All"),
                new Gender(Query.GENDER.FEMALE, "Female"),
                new Gender(Query.GENDER.MALE, "Male")
        );

        ageCombo.getItems().addAll(
                new Age(Query.AGE.ALL, "All"),
                new Age(Query.AGE.LT_25, "< 25"),
                new Age(Query.AGE._25_TO_34, "25 to 34"),
                new Age(Query.AGE._35_TO_44, "35 to 44"),
                new Age(Query.AGE._45_TO_54, "45 to 54"),
                new Age(Query.AGE.GT_54, "> 54")
        );

        incomeCombo.getItems().addAll(
                new Income(Query.INCOME.ALL, "All"),
                new Income(Query.INCOME.HIGH, "High"),
                new Income(Query.INCOME.MEDIUM, "Medium"),
                new Income(Query.INCOME.LOW, "Low")
        );

        contextCombo.getItems().addAll(
                new Context(Query.CONTEXT.ALL, "All"),
                new Context(Query.CONTEXT.BLOG, "Blog"),
                new Context(Query.CONTEXT.HOBBIES, "Hobbies"),
                new Context(Query.CONTEXT.NEWS, "News"),
                new Context(Query.CONTEXT.SHOPPING, "Shopping"),
                new Context(Query.CONTEXT.SOCIAL_MEDIA, "Social Media"),
                new Context(Query.CONTEXT.TRAVEL, "Travel")
        );

        initialiseEventListeners();
    }

    protected void initialiseEventListeners() {
        hourlyButton.setOnAction(this::triggerUpdateEvent);
        dailyButton.setOnAction(this::triggerUpdateEvent);
        monthlyButton.setOnAction(this::triggerUpdateEvent);

        startDate.setOnAction(this::triggerUpdateEvent);
        endDate.setOnAction(this::triggerUpdateEvent);
        genderCombo.setOnAction(this::triggerUpdateEvent);
        ageCombo.setOnAction(this::triggerUpdateEvent);
        incomeCombo.setOnAction(this::triggerUpdateEvent);
        contextCombo.setOnAction(this::triggerUpdateEvent);

        bounceToggle.setOnAction(this::bounceToggle);
    }

    public Query getQuery(Query.TYPE type) {
        lastQuery = type;

        if (type == Query.TYPE.BOUNCES) {
            if(isBounceTime){
                type = Query.TYPE.BOUNCES_TIME;
            } else {
                type = Query.TYPE.BOUNCES_PAGES;
            }
        } else if (type == Query.TYPE.BOUNCE_RATE) {
            if(isBounceTime){
                type = Query.TYPE.BOUNCE_RATE_TIME;
            } else {
                type = Query.TYPE.BOUNCE_RATE_PAGES;
            }
        }

        Timestamp from = getFromDate();
        Timestamp to = getToDate();

        Query.RANGE range = getRange();
        Query.GENDER gender = getGender();
        Query.INCOME income = getIncome();
        Query.AGE age = getAge();
        Query.CONTEXT context = getContext();

        return new Query(type, range, from, to, gender, age, income, context);
    }

    public void addUpdateListener(FilterUpdateListener listener) {
        listenerList.add(listener);
    }

    public void removeUpdateListener(FilterUpdateListener listener) {
        listenerList.remove(listener);
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

    public Query.GENDER getGender() {
        Query.GENDER gender = Query.GENDER.ALL;
        if (genderCombo.getSelectionModel().getSelectedItem() != null) {
            gender = genderCombo.getSelectionModel().getSelectedItem().getType();
        }
        return gender;
    }

    public Query.INCOME getIncome() {
        Query.INCOME income = Query.INCOME.ALL;
        if (incomeCombo.getSelectionModel().getSelectedItem() != null) {
            income = incomeCombo.getSelectionModel().getSelectedItem().getType();
        }
        return income;
    }

    public Query.AGE getAge() {
        Query.AGE age = Query.AGE.ALL;
        if (ageCombo.getSelectionModel().getSelectedItem() != null) {
            age = ageCombo.getSelectionModel().getSelectedItem().getType();
        }
        return age;
    }

    public Query.CONTEXT getContext() {
        Query.CONTEXT context = Query.CONTEXT.ALL;
        if (contextCombo.getSelectionModel().getSelectedItem() != null) {
            context = contextCombo.getSelectionModel().getSelectedItem().getType();
        }
        return context;
    }

    public String getRangeString() {
        Query.RANGE range = getRange();
        return range.toString().substring(0, 1).toUpperCase() + range.toString().substring(1).toLowerCase();
    }

    protected void bounceToggle(ActionEvent e) {
        isBounceTime = !isBounceTime;
        bounceToggle.setText(isBounceTime ? "Time < 60s" : "1 Page Viewed");
        if(lastQuery != Query.TYPE.BOUNCES && lastQuery != Query.TYPE.BOUNCE_RATE) return;
        this.triggerUpdateEvent(e);
    }
}
