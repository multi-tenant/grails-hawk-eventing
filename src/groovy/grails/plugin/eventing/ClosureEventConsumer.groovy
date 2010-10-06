package grails.plugin.eventing

import groovy.lang.Closure;

class ClosureEventConsumer implements EventConsumer {

	protected String name
	protected Closure eventClosure
	
	public ClosureEventConsumer(String name, Closure eventClosure) {
		this.name = name
		this.eventClosure = eventClosure
	}
	
	public void consume(Object event, EventBroker broker = null) {
		def args = [ event ]
		if (expectsTwoArguments(eventClosure))
			args << broker
			
		eventClosure.call((Object[]) args)
	}
	
	private boolean expectsTwoArguments(Closure closure) {
		closure.getMaximumNumberOfParameters() == 2
	}
	
	public String getName() {
		return name
	}
	
}