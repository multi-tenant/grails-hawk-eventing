package grails.plugins.hawkeventing;

import java.util.Set;

/**
 * @author Kim A. Betti
 */
public interface EventConsumerConfiguration {

    Set<EventSubscription> getSubscriptions();

}
