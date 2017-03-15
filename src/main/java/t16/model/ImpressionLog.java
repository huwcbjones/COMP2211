package t16.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

import static t16.model.Query.*;

/**
 * An Impression
 *
 * @author Huw Jones
 * @since 06/03/2017
 */
public class ImpressionLog {

    private Timestamp date;
    private long id;
    private GENDER gender;
    private String age;
    private INCOME income;
    private CONTEXT context;
    private BigDecimal cost;

    public ImpressionLog(Timestamp date, long id, GENDER gender, String age, INCOME income, CONTEXT context, BigDecimal cost) {
        this.date = date;
        this.id = id;
        this.gender = gender;
        this.age = age;
        this.income = income;
        this.context = context;
        this.cost = cost;
    }

    public Timestamp getDate() {
        return date;
    }

    public long getId() {
        return id;
    }

    public GENDER getGender() {
        return gender;
    }

    public String getAge() {
        return age;
    }

    public INCOME getIncome() {
        return income;
    }

    public CONTEXT getContext() {
        return context;
    }

    public BigDecimal getCost() {
        return cost;
    }

}
