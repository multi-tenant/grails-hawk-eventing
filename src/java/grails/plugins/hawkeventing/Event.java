package grails.plugins.hawkeventing;

import java.util.Date;

/**
 * 
 * @author Kim A. Betti
 */
public interface Event {

    Object getPayload();

    String getEventName();

    Date getCreatedAt();

}