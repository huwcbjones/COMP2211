package t16.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
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
public class IndividualFilter extends VBox {

    //<editor-fold desc="View Components">
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

    public IndividualFilter() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/IndividualFilter.fxml"));
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
        genderCombo.setOnAction(this::triggerUpdateEvent);
        ageCombo.setOnAction(this::triggerUpdateEvent);
        incomeCombo.setOnAction(this::triggerUpdateEvent);
        contextCombo.setOnAction(this::triggerUpdateEvent);
        bounceToggle.setOnAction(this::bounceToggle);
    }

    public Query getSpecificQuery(Query.TYPE type, Query.RANGE range, Timestamp from, Timestamp to) {
        lastQuery = type;

        if (type == Query.TYPE.BOUNCES) {
            if (isBounceTime) {
                type = Query.TYPE.BOUNCES_TIME;
            } else {
                type = Query.TYPE.BOUNCES_PAGES;
            }
        } else if (type == Query.TYPE.BOUNCE_RATE) {
            if (isBounceTime) {
                type = Query.TYPE.BOUNCE_RATE_TIME;
            } else {
                type = Query.TYPE.BOUNCE_RATE_PAGES;
            }
        }

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

    protected void bounceToggle(ActionEvent e) {
        isBounceTime = !isBounceTime;
        bounceToggle.setText(isBounceTime ? "Time < 30s" : "1 Page Viewed");
        if (lastQuery != Query.TYPE.BOUNCES && lastQuery != Query.TYPE.BOUNCE_RATE) return;
        this.triggerUpdateEvent(e);
    }

    public String toString() {
        String s = "";
        Gender gender = this.genderCombo.getValue();
        if (gender != null && gender.getType() != Query.GENDER.ALL) {
            s += gender.toString() + "s";
        }
        Income income = this.incomeCombo.getValue();
        if (income != null && income.getType() != Query.INCOME.ALL) {
            s += (s.isEmpty() ? "" : "; ") + income.toString() + " income";
        }
        Age age = this.ageCombo.getValue();
        if (age != null && age.getType() != Query.AGE.ALL) {
            s += (s.isEmpty() ? "" : "; ") + "Aged " + age.toString();
        }
        Context context = this.contextCombo.getValue();
        if (context != null && context.getType() != Query.CONTEXT.ALL) {
            s += (s.isEmpty() ? "" : "; ") + context.toString() + " context";
        }
        return (s.isEmpty() ? "No filters. Bounce is " : s + ". Bounce is ") + (isBounceTime ? "time spent" : "pages viewed");
    }
}
