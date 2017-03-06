package t16.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * An Impression
 *
 * @author Huw Jones
 * @since 06/03/2017
 */
public class ImpressionLog {

    private Timestamp date;
    private long id;
    private Gender gender;
    private String age;
    private Income income;
    private Context context;
    private BigDecimal cost;

    public ImpressionLog(Timestamp date, long id, Gender gender, String age, Income income, Context context, BigDecimal cost) {
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

    public Gender getGender() {
        return gender;
    }

    public String getAge() {
        return age;
    }

    public Income getIncome() {
        return income;
    }

    public Context getContext() {
        return context;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public enum Gender {
        MALE,
        FEMALE
    }

    public enum Income {
        LOW,
        MEDIUM,
        HIGH
    }

    public enum Context {
        NEWS,
        SHOPPING,
        SOCIAL_MEDIA,
        BLOG,
        HOBBIES,
        TRAVEL
    }
}
