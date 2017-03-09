package t16.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 *  A Click
 *
 * @author Huw Jones
 * @since 06/03/2017
 */
public class ClickLog {

    private Timestamp date;
    private long id;
    private BigDecimal cost;

    public ClickLog(Timestamp date, long id, BigDecimal cost) {
        this.date = date;
        this.id = id;
        this.cost = cost;
    }

    public Timestamp getDate() {
        return date;
    }

    public long getId() {
        return id;
    }

    public BigDecimal getCost() {
        return cost;
    }
}
