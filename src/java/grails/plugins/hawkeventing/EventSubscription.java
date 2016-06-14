package grails.plugins.hawkeventing;

/**
 * @author Kim A. Betti
 */
public class EventSubscription {

    private final String eventName;
    private final EventConsumer consumer;

    public EventSubscription(String eventName, EventConsumer consumer) {
        this.eventName = eventName;
        this.consumer = consumer;
    }

    public String getEventName() {
        return eventName;
    }

    public EventConsumer getConsumer() {
        return consumer;
    }

}