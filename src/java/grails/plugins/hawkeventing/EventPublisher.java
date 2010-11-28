package grails.plugins.hawkeventing;

import java.util.concurrent.Future;

/**
 * @author Kim A. Betti
 */
public interface EventPublisher {

	Future<?> publish(Event event, EventConsumer consumer);
	
}