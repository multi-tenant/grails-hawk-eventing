package grails.plugins.hawkeventing;

import java.util.Iterator;

/**
 * Used with event bubbling. It takes a full event name
 * and spits of out all the event names that should receive
 * the event.
 * 
 * Example: hibernate.save.book should be published to
 * ---------------------------------------------------
 *  - hibernate.save.book
 *  - hibernate.save
 *  - hibernate
 * 
 * @author Kim A. Betti
 */
public class EventNameDecoder implements Iterator<String> {

    public final char EVENT_SEPARATOR_CHAR = '.';

    private final String fullEventName;
    private int dotIdx = 0;

    public EventNameDecoder(String fullEventName) {
        this.fullEventName = fullEventName;
    }

    @Override
    public boolean hasNext() {
        return dotIdx < fullEventName.length();
    }

    @Override
    public String next() {
        int endIndex = fullEventName.indexOf(EVENT_SEPARATOR_CHAR, dotIdx);
        if (endIndex < 0) {
            endIndex = fullEventName.length();
        }

        String eventName = fullEventName.substring(0, endIndex);
        dotIdx = endIndex + 1;

        return eventName;
    }

    @Override
    public void remove() {
        throw new RuntimeException("Remove is not supported");
    }

}
