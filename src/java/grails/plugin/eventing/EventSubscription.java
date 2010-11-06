package grails.plugin.eventing;

/**
 * 
 * @author Kim A. Betti
 */
public class EventSubscription {

	final String eventName;
	final EventConsumer consumer;
	
	public EventSubscription(String eventName, EventConsumer consumer) {
		super();
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
