package t16.exceptions;

/**
 * {DESCRIPTION}
 *
 * @author Huw Jones
 * @since 14/03/2017
 */
public class CampaignLoadException extends Exception {

    public CampaignLoadException(String message) {
        super(message);
    }
    public CampaignLoadException(String message, Throwable cause) {
        super(message, cause);
    }
}
