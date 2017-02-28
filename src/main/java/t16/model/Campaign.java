package t16.model;

import java.io.File;

/**
 * A Campaign
 *
 * @author Huw Jones
 * @since 25/02/2017
 * Modified by James Curran 28/2/17
 * TODO Decide on format and properties
 */
public class Campaign {
    private String name;

    public Campaign(String name)
    {
        this.name = name;
    }

    public static Campaign fromFile(File dbFile)
    {
        return new Campaign(dbFile.getName());
    }

    public String getName()
    {
        return this.name;
    }
}
