package grails.plugin.eventing;

import java.util.Iterator;

/**
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
		if (endIndex < 0) 
			endIndex = fullEventName.length();
		
		String eventName = fullEventName.substring(0, endIndex);
		dotIdx = endIndex + 1;
		
		return eventName;
	}

	@Override
	public void remove() {
		throw new RuntimeException("Remove is not supported");
	}
	
}
