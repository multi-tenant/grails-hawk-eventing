package demo

import grails.plugins.hawkeventing.Event;
import grails.plugins.hawkeventing.annotation.Consuming;

class EventDemoService {

    static transactional = false
	
	String latestBook = null

	@Consuming("book.created")
    def bookCreated(Event event) {
		latestBook = event.getPayload()
    }
		
}
