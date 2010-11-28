package grails.plugins.hawkeventing;

import groovy.lang.Closure;

/**
 * 
 * @author Kim A. Betti
 */
class ClosureEventConsumer implements EventConsumer {

	protected final Closure eventClosure;
	protected final String consumerName;
	
	public ClosureEventConsumer(Closure eventClosure) {
		this.eventClosure = eventClosure;
		this.consumerName = "no-name";
	}
	
	public ClosureEventConsumer(Closure eventClosure, String consumerName) {
		this.eventClosure = eventClosure;
		this.consumerName = consumerName;
	}
	
	public void consume(Event event) {
		eventClosure.call(event);
	}
	
	public String getName() {
		return consumerName;
	}
	
}