package grails.plugins.hawkeventing;

import java.util.concurrent.Future;

/**
 * 
 * @author Kim A. Betti
 */
public class SyncEventPublisher implements EventPublisher {
	
	private final Future<Object> nullFuture = new NullFuture();

	@Override
	public Future<?> publish(Event event, EventConsumer consumer) {
		consumer.consume(event);
		return nullFuture;
	}
	
}