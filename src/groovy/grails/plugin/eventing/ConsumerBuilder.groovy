package grails.plugin.eventing

import grails.plugin.eventing.exceptions.InvalidEventConfigurationException

/**
 * See tests for usage.
 * 
 * TODO: give a better name
 * 
 * @author Kim A. Betti <kim.betti@gmail.com>
 */
class ConsumerBuilder implements EventConsumerConfiguration {
	
	protected Closure configurationClosure
	protected Object closureArgument
	protected Map<String, EventConsumer> consumers = [:]
	protected List currentEventHierarchi = []
	 
	protected ConsumerBuilder(Closure configurationClosure, Object ctx) {
		this.configurationClosure = configurationClosure
		this.closureArgument = ctx
	}
	
	public static ConsumerBuilder fromClosure(Closure configurationClosure, Object ctx = null) {
		return new ConsumerBuilder(configurationClosure, ctx)
	}
	
	public Map<String, List<EventConsumer>> getConsumers() {
		invokeConfgurationClosure()
		return this.consumers
	}
	
	private void invokeConfgurationClosure(Closure cfgClosure) {
		configurationClosure.delegate = this
		configurationClosure.call(closureArgument)
	}

	void methodMissing(String name, Object arguments) {
		String fullEventName = getFullEventName(name)
		addEventConsumer(fullEventName, (Object[]) arguments)
	}
	
	private String getFullEventName(String lastName) {
		currentEventHierarchi << lastName
		String fullName = currentEventHierarchi.join(EventBroker.EVENT_SEPARATOR)
		currentEventHierarchi.clear()
		return fullName
	}
	
	private void addEventConsumer(String fullEventName, Object[] arguments) {
		hasAtLeastOneArgument(arguments)
		Closure eventHandler = getEventHandler(arguments)
		String consumerName = getConsumerName(arguments)
		EventConsumer eventConsumer = new ClosureEventConsumer(consumerName, eventHandler)
		addEventConsumerToMap(fullEventName, eventConsumer)
	}
	
	private void addEventConsumerToMap(String fullEventName, EventConsumer eventConsumer) {
		if (!consumers.containsKey(fullEventName))
			consumers[fullEventName] = []
		
		consumers[fullEventName] << eventConsumer
	}
	
	private void hasAtLeastOneArgument(Object[] arguments) {
		if (arguments.length == 0) {
			String msg = "No arguments supplied for event $eventName, expected (at least) a closure"
			throw new InvalidEventConfigurationException(msg)
		}
	}
	
	private Closure getEventHandler(Object[] arguments) {
		Object lastArgument = arguments[arguments.length-1]
		if (!isClosure(lastArgument)) {
			String msg = "Expected the last argument to be a closure, not " + lastArgument.getClass().name
			throw new InvalidEventConfigurationException(msg)
		}
		
		return lastArgument
	}
	
	private String getConsumerName(Object[] arguments) {
		String consumerName = "anonymous"
		if (arguments.length > 1 && arguments[0] instanceof String) {
			consumerName = arguments[0]
			if (consumerName.length() == 0) {
				String msg = "Event consumer name can't be empty"
				throw new InvalidEventConfigurationException(msg)
			}
		}
			
		return consumerName
	}
	
	private boolean isClosure(Object argument) {
		return argument instanceof Closure
	}
	
	def propertyMissing(String name) {
		currentEventHierarchi << name
		return this
	}
	
}
