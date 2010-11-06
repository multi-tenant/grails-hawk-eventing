package demo

import grails.plugin.eventing.Event;
import grails.plugin.eventing.annotation.Consuming;

class EventDemoService {

    static transactional = false
	
	String latestBook = null

	@Consuming("book.created")
    def bookCreated(Event event) {
		latestBook = event.getPayload()
    }
		
}
