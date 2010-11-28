package grails.plugin.eventing

import grails.plugin.eventing.exceptions.EventException;
import grails.plugin.spock.IntegrationSpec;

class AsyncEventPublisherSpec extends IntegrationSpec {
	
	def syncEvent = new BaseEvent("hibernate.newSession", null, false)
	def publisher = new AsyncEventPublisher()

	def "Should throw exception without executorService"() {
		when: publisher.publish null, null
		then: thrown(EventException)
	}
	
	def "Should throw exception for sync-events"() {
		given: publisher.executorService = [ submit: {} ]
		when: publisher.publish syncEvent, null
		then: thrown(EventException)
	}
	
}
