package grails.plugins.hawkeventing;

import groovy.lang.Closure;
import java.util.*;
import java.util.concurrent.Future;

/**
 * 
 * @author Kim A. Betti
 */
public class EventBroker {

	private Map<String, Set<EventConsumer>> eventConsumers
		= Collections.synchronizedMap(new HashMap<String, Set<EventConsumer>>());
	
	private EventPublisher syncPublisher, asyncPublisher;

	public void subscribe(Set<EventSubscription> subscriptions) {
		for (EventSubscription subscription : subscriptions) 
			subscribe(subscription);
	}
	
	public void subscribe(String eventName, Closure eventClosure) {
		ClosureEventConsumer consumer = new ClosureEventConsumer(eventClosure);
		subscribe(eventName, consumer);
	}
	
	public void subscribe(EventSubscription subscription) {
		String eventName = subscription.getEventName();
		subscribe(eventName, subscription.getConsumer());
	}
	
	public void subscribe(String eventName, EventConsumer consumer) {
		ensureKeyFor(eventName);
		eventConsumers.get(eventName).add(consumer);
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
	
	public void publishAsync(String eventName, Object payload) {
		Event event = new BaseEvent(eventName, payload, true);
		publishEvent(event);
	}
	
	public Set<Future<?>> publishEvent(Event event) {
		Set<Future<?>> futures = new HashSet<Future<?>>();
		String fullEventName = event.getEventName();
		EventNameDecoder eventNameDecoder = new EventNameDecoder(fullEventName);
		EventPublisher publisher = event.isAsync() ? asyncPublisher : syncPublisher;
		
		while (eventNameDecoder.hasNext()) {
			String currentEventName = eventNameDecoder.next();
			Set<EventConsumer> consumers = getEventConsumers(currentEventName);
			for (EventConsumer consumer : consumers)
				publisher.publish(event, consumer);
		}
		
		return futures;
	}
	
	private Set<EventConsumer> getEventConsumers(String eventName) {
		Set<EventConsumer> consumers = Collections.emptySet();
		if (eventConsumers.containsKey(eventName))
			consumers = eventConsumers.get(eventName);
		
		return consumers;
	}
	
	public void setSyncEventPublisher(EventPublisher publisher) {
		this.syncPublisher = publisher;
	}
	
	public void setAsyncEventPublisher(EventPublisher publisher) {
		this.asyncPublisher = publisher;
	}
	
}
