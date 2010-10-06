package grails.plugin.eventing

import grails.plugin.eventing.exceptions.InvalidEventConfigurationException
import grails.plugin.spock.*

/**
*
* @author Kim A. Betti <kim.betti@gmail.com>
*/
class ConsumerBuilderSpec extends UnitSpec {
	
	def "Single event definition"() {
		when:
		Map evtConsumerMap = ConsumerBuilder.fromClosure {
			hibernate.book.delete { book -> }
		}.consumers
		
		then: "Found one consumer with the expected name"
		evtConsumerMap.size() == 1
		evtConsumerMap.containsKey "hibernate.book.delete"
	}
	
	def "Multiple listeners for single event"() {
		when:
		Map evtConsumerMap = ConsumerBuilder.fromClosure {
			hibernate.book.delete { book -> }
			hibernate.book.delete { book -> }
		}.consumers
		
		then: "Found one consumer with the expected name"
		evtConsumerMap.size() == 1
		evtConsumerMap.containsKey "hibernate.book.delete"
		evtConsumerMap.get("hibernate.book.delete").size() == 2
	}

	def "Multiple event definitions for different events"() {
		when:
		Map evtConsumerMap = ConsumerBuilder.fromClosure {
			hibernate.book.delete { book -> }
			hibernate.sessionCreated { session -> }
			customEvent {  }
		}.consumers
		
		then: "Three consumers are added"
		evtConsumerMap.size() == 3
		
		and: "With the correct event names"
		evtConsumerMap.containsKey "hibernate.book.delete"
		evtConsumerMap.containsKey "hibernate.sessionCreated"
		evtConsumerMap.containsKey "customEvent"
	}
	
	def "Event definition with name"() {
		when:
		Map evtConsumerMap = ConsumerBuilder.fromClosure {
			hibernate.book.delete "remove-from-library", { book -> }
		}.consumers
		
		then: "The consumers has the expected names"
		evtConsumerMap.containsKey "hibernate.book.delete"
		evtConsumerMap.get("hibernate.book.delete").get(0).name == "remove-from-library"
	}
	
	def "Event definitions without name are named anonymous"() {
		when:
		Map evtConsumerMap = ConsumerBuilder.fromClosure {
			hibernate.book.delete { book -> }
		}.consumers
		
		then: "The consumer is named anonymous"
		evtConsumerMap.containsKey "hibernate.book.delete"
		println "Event consumer:"  + evtConsumerMap.get("hibernate.book.delete").get(0).getClass().name
		evtConsumerMap.get("hibernate.book.delete").get(0).name == "anonymous"
	}
	
	def "Empty name triggers exception"() {
		when:
		Map evtConsumerMap = ConsumerBuilder.fromClosure {
			hibernate.book.delete "", { book -> }
		}.consumers
		
		then: "An exception is thrown"
		thrown(InvalidEventConfigurationException)
	}
	
}
