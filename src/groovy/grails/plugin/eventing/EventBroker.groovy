package grails.plugin.eventing

/**
*
* @author Kim A. Betti <kim.betti@gmail.com>
*/
class EventBroker {

	public static final String EVENT_SEPARATOR = "."
	
	protected Map eventConsumerMap
	
	public EventBroker() {
		eventConsumerMap = Collections.synchronizedMap(new HashMap<String, Set<EventConsumer>>())
	}
	
	public void addSubscriptionsFromConfiguration(EventConsumerConfiguration cfg) {
		Map evtConsumerMap = cfg.getConsumers()
		evtConsumerMap.each { String fullEventName, List consumers ->
			consumers.each { EventConsumer consumer ->
				subscribe(fullEventName, consumer)
			}
		} 
	}
	
	public void subscribe(String fullEventName, Closure eventClosure) {
		ClosureEventConsumer eventConsumer = new ClosureEventConsumer(null, eventClosure);
		subscribe fullEventName, eventConsumer	
	}
	
	public void subscribe(String fullEventName, EventConsumer eventConsumer) {
		if (!eventConsumerMap.containsKey(fullEventName)) 
			eventConsumerMap[fullEventName] = new LinkedHashSet<EventConsumer>()
		
		eventConsumerMap[fullEventName] << eventConsumer
	}
	
	public void unsubscribe(String fullEventName, EventConsumer eventConsumer) {
		Set consumers = getEventConsumers(fullEventName)
		consumers.remove(eventConsumer)
		if (consumers.size() == 0)
			eventConsumerMap.remove(fullEventName)
	}
	
	public void publish(String eventName, Object event) {
		getConsumersForEventNamed(eventName)*.consume(event, this)
	}
	
	private List<EventConsumer> getConsumersForEventNamed(String eventName) {
		List consumers = []
		while (eventName) {
			consumers.addAll(getEventConsumers(eventName))
			int lastDotIndex = eventName.lastIndexOf(EVENT_SEPARATOR)
			eventName = lastDotIndex > 0 ? eventName.substring(0, lastDotIndex) : null
		}
		
		return consumers
	}
		
	public Set getEventConsumers(String fullEventName) {
		(eventConsumerMap.containsKey(fullEventName)
			? eventConsumerMap[fullEventName]
			: Collections.emptySet())
	}
	
}