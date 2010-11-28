package grails.plugins.hawkeventing

import grails.plugins.hawkeventing.exceptions.EventException;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * 
 * @author Kim A. Betti
 */
class AsyncEventPublisher implements EventPublisher {
	
	def executorService

	@Override
	public Future<?> publish(Event event, EventConsumer consumer) {
		if (!executorService) {
			throw new EventException("Async event publishing is not supported by this installation")
		} else if (!event.isAsync()) { 
			throw new EventException("Tried async publishing of non-async event: " + event)	
		}
		
		return executorService.submit {
			consumer.consume event
		} as Callable<?>
	}
	
}