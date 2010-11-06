package grails.plugin.eventing;

import java.util.Date;

/**
 * 
 * @author Kim A. Betti
 */
public class BaseEvent implements Event {

	final boolean async;
	final Object payload;
	final String eventName;
	final Date createdAt;
	
	public BaseEvent(String eventName, Object payload) {
		this.payload = payload;
		this.eventName = eventName;
		this.createdAt = new Date();
		this.async = false;
	}
	
	public BaseEvent(String eventName, Object payload, boolean async) {
		this.payload = payload;
		this.eventName = eventName;
		this.createdAt = new Date();
		this.async = async;
	}

	@Override
	public Object getPayload() {
		return payload;
	}

	@Override
	public String getEventName() {
		return eventName;
	}

	@Override
	public Date getCreatedAt() {
		return createdAt;
	}
	
	@Override
	public boolean isAsync() {
		return async;
	}
	
	public String toString() {
		StringBuffer buffer = new StringBuffer(100);
		if (isAsync()) 
			buffer.append("Async");
		
		buffer.append("Event[").append(eventName).append("]");
		buffer.append(", payload: " + payload);
		return buffer.toString();
	}
	
}