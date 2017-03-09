package t16.model;

import java.sql.Timestamp;

/**
 * A Server Log
 *
 * @author Huw Jones
 * @since 06/03/2017
 */
public class ServerLog {

    private Timestamp entry;
    private long id;
    private Timestamp exit;
    private long pageViews;
    private boolean isConversion;

    public ServerLog(Timestamp entry, long id, Timestamp exit, long pageViews, boolean isConversion) {
        this.entry = entry;
        this.id = id;
        this.exit = exit;
        this.pageViews = pageViews;
        this.isConversion = isConversion;
    }

    public Timestamp getEntry() {
        return entry;
    }

    public long getId() {
        return id;
    }

    public Timestamp getExit() {
        return exit;
    }

    public long getPageViews() {
        return pageViews;
    }

    public boolean isConversion() {
        return isConversion;
    }
}
