package grails.plugins.hawkeventing;

import java.util.Date;

/**
 * 
 * @author Kim A. Betti
 */
public class BaseEvent implements Event {

    final Object payload;
    final String eventName;
    final Date createdAt;

    public BaseEvent(String eventName, Object payload) {
        this.payload = payload;
        this.eventName = eventName;
        this.createdAt = new Date();
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
    public String toString() {
        StringBuffer buffer = new StringBuffer(100);
        buffer.append("Event[").append(eventName).append("]");
        buffer.append(", payload: " + payload);
        return buffer.toString();
    }

}