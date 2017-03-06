package t16.exceptions;

/**
 * Campaign Creation Exception
 *
 * @author Huw Jones
 * @since 01/03/2017
 */
public class CampaignCreationException extends Exception {

    public CampaignCreationException(String message) {
        super(message);
    }
    public CampaignCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}
