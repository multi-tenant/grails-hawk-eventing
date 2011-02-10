package demo

import grails.plugins.hawkeventing.Event;
import grails.plugins.hawkeventing.annotation.Consuming;
import grails.plugins.hawkeventing.annotation.HawkEventConsumer;

@HawkEventConsumer
class EventDemoService {

    static transactional = false
	
	String latestBook = null

	@Consuming("book.created")
    def bookCreated(Event event) {
		latestBook = event.getPayload()
    }
		
}
