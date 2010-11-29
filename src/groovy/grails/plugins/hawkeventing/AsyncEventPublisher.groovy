package grails.plugins.hawkeventing

import grails.plugins.hawkeventing.exceptions.EventException;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationContext;

/**
 * 
 * @author Kim A. Betti
 */
class AsyncEventPublisher implements EventPublisher, ApplicationContextAware {
	
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
	
	/**
	 * Having executorService injected by name screwed up the
	 * load order for hibernate-hijacker and multi-tenant-core.
	 * @param ctx
	 */
	void setApplicationContext(ApplicationContext ctx) {
		if (ctx.containsBean("executor")) {
			executorService = ctx.getBean("executorService")
		}
	}
	
}