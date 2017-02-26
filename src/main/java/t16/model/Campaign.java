package t16.model;

import java.util.UUID;

/**
 * A Campaign
 *
 * @author Huw Jones
 * @since 25/02/2017
 */
public class Campaign {

    private UUID id;
    private String name;

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
