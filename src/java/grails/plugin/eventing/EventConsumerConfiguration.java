package grails.plugin.eventing;

import java.util.List;
import java.util.Map;

public interface EventConsumerConfiguration {

	Map<String, List<EventConsumer>> getConsumers();
	
}
