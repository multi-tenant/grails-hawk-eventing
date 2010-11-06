package grails.plugin.eventing;

import java.util.*;

/**
 * 
 * @author Kim A. Betti
 */
public class EventBroker {

	private Map<String, Set<EventConsumer>> eventConsumers
		= Collections.synchronizedMap(new HashMap<String, Set<EventConsumer>>());

	public void subscribe(EventSubscription subscription) {
		String eventName = subscription.getEventName();
		ensureKeyFor(eventName);
		eventConsumers.get(eventName).add(subscription.getConsumer());
	}
	
	private void ensureKeyFor(String eventName) {
		if (!eventConsumers.containsKey(eventName))
			eventConsumers.put(eventName, new HashSet<EventConsumer>());
	}
	
	public void unsubscribe(String eventName, EventConsumer eventConsumer) {
		Set<EventConsumer> consumers = getEventConsumers(eventName);
		consumers.remove(eventConsumer);
	}
	
	public void publish(String eventName, Object payload) {
		Event event = new BaseEvent(eventName, payload);
		publishEvent(event);
	}
	
	public void publishEvent(Event event) {
		String fullEventName = event.getEventName();
		EventNameDecoder eventNameDecoder = new EventNameDecoder(fullEventName);
		while (eventNameDecoder.hasNext()) {
			String currentEventName = eventNameDecoder.next();
			System.out.println("Current event name: " + currentEventName);
			Set<EventConsumer> consumers = getEventConsumers(currentEventName);
			for (EventConsumer consumer : consumers) {
				System.out.println(currentEventName + " to " + consumer);
				consumer.consume(event);
			} 
		}
	}
	
	private Set<EventConsumer> getEventConsumers(String eventName) {
		Set<EventConsumer> consumers = Collections.emptySet();
		if (eventConsumers.containsKey(eventName))
			consumers = eventConsumers.get(eventName);
		
		return consumers;
	}
	
}
