package t16.model;

/**
 * {DESCRIPTION}
 *
 * @author Huw Jones
 * @since 15/03/2017
 */
public class Income {
    private Query.INCOME type;
    private String income;

    public Income(Query.INCOME type, String income) {
        this.type = type;
        this.income = income;
    }

    public Query.INCOME getType() {
        return type;
    }

    public String getIncome() {
        return income;
    }

    @Override
    public String toString() {
        return income;
    }
}