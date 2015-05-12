package grails.plugins.hawkeventing;

import groovy.lang.Closure;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 
 * @author Kim A. Betti
 */
public class EventBroker {

    private Map<String, Set<EventConsumer>> eventConsumers = Collections.synchronizedMap(new HashMap<String, Set<EventConsumer>>());

    private EventPublisher eventPublisher;

    public void subscribe(Set<EventSubscription> subscriptions) {
        for (EventSubscription subscription : subscriptions) {
            subscribe(subscription);
        }
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
        if (!eventConsumers.containsKey(eventName)) {
            eventConsumers.put(eventName, new HashSet<EventConsumer>());
        }
    }

    public void unsubscribe(String eventName, EventConsumer eventConsumer) {
        Set<EventConsumer> consumers = getEventConsumers(eventName);
        consumers.remove(eventConsumer);
    }

    /**
     * Use this method to publish a basic event with any kind
     * of payload. The payload will be wrapped in a BaseEvent
     * before published to any subscribers.
     * @param eventName
     * @param payload
     */
    public void publish(String eventName, Object payload) {
        Event event = new BaseEvent(eventName, payload);
        publishEvent(event);
    }

    public void publishEvent(Event event) {
        String fullEventName = event.getEventName();
        EventNameDecoder eventNameDecoder = new EventNameDecoder(fullEventName);

        while (eventNameDecoder.hasNext()) {
            String currentEventName = eventNameDecoder.next();
            Set<EventConsumer> consumers = getEventConsumers(currentEventName);
            for (EventConsumer consumer : consumers) {
                eventPublisher.publish(event, consumer);
            }
        }
    }

    private Set<EventConsumer> getEventConsumers(String eventName) {
        Set<EventConsumer> consumers = Collections.emptySet();
        if (eventConsumers.containsKey(eventName)) {
            consumers = eventConsumers.get(eventName);
        }

        return consumers;
    }

    public void setEventPublisher(EventPublisher publisher) {
        this.eventPublisher = publisher;
    }

}
