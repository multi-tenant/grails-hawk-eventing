package grails.plugins.hawkeventing;

import grails.plugins.hawkeventing.exceptions.EventException;

import java.lang.reflect.Method;

/**
 * 
 * @author Kim A. Betti
 */
public class MethodEventConsumer implements EventConsumer {

	private final Method method;
	private final Object consumer;

	public MethodEventConsumer(Object consumer, Method method) {
		this.consumer = consumer;
		this.method = method;
	}

	@Override
	public void consume(Event event) {
		try {
			method.invoke(consumer, event);
		} catch (Exception ex) {
			throw new EventException("Unable to notify " + consumer + " of " + event + ", ex: " + ex.getMessage(), ex);
		}
	}
	
}
