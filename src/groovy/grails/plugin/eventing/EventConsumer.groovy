package grails.plugin.eventing;

public interface EventConsumer {

	void consume(Object event, EventBroker broker);
	
	String getName();
	
}