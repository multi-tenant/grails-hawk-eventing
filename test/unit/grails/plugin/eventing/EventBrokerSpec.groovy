package grails.plugin.eventing

import grails.plugin.spock.*

/**
 * 
 * @author Kim A. Betti <kim.betti@gmail.com>
 */
class EventBrokerSpec extends UnitSpec {

	def broker
	
	def setup() {
		broker = new EventBroker()
		broker.syncEventPublisher = new SyncEventPublisher()
	}

	def "unsubscribe"() {
		when: "We add a new subscription"
			broker.subscribe(new EventSubscription("book.created", new NullConsumer()))
			Set consumers = broker.getEventConsumers('book.created')
		
		then: "Number of subscriptions are one"
			consumers.size() == 1
		
		when: "We unsubscribe"
			broker.unsubscribe('book.created', consumers.toArray()[0])
			consumers = broker.getEventConsumers('book.created')
		
		then: "There should be zero subscribers left for the event"
			consumers.size() == 0
	}
	
	def "Event recieved by single consumer"() {
		given:
		def consumer = Mock(EventConsumer)
		
		when:
		broker.subscribe(new EventSubscription("book.created", consumer))
		broker.publish("book.created", "Groovy in action")
		
		then:
		1 * consumer.consume(_ as Event)
	}
	
	def "Events are only recieved by the expected consumers"() {
		given:
			def bookConsumer = Mock(EventConsumer)
			def secondBookConsumer = Mock(EventConsumer)
			def authorConsumer = Mock(EventConsumer)
		
		when:
			broker.subscribe(new EventSubscription("book.created", bookConsumer))
			broker.subscribe(new EventSubscription("book.created", secondBookConsumer))
			broker.subscribe(new EventSubscription("author.created", authorConsumer))
			broker.publish("book.created", "Groovy in action")
		
		then:
			1 * bookConsumer.consume(_ as Event)
			1 * secondBookConsumer.consume(_ as Event)
			0 * authorConsumer.consume(_ as Event)
	}
	
	def "Event bubbling"() {
		given:
			def hibernateConsumer = Mock(EventConsumer)
			def bookConsumer = Mock(EventConsumer)
			def bookCreatedConsumer = Mock(EventConsumer)
			def bookDeletedConsumer = Mock(EventConsumer)
		
		when: "we subscribe"
			broker.subscribe(new EventSubscription("hibernate", hibernateConsumer))
			broker.subscribe(new EventSubscription("hibernate.book", bookConsumer))
			broker.subscribe(new EventSubscription("hibernate.book.created", bookCreatedConsumer))
			broker.subscribe(new EventSubscription("hibernate.book.deleted", bookDeletedConsumer))
			
		and: "publish the event"
			broker.publish("hibernate.book.created", "Groovy in action")
		
		then:
			1 * hibernateConsumer.consume(_ as Event)
			1 * bookConsumer.consume(_ as Event)
			1 * bookCreatedConsumer.consume(_ as Event)
			0 * bookDeletedConsumer.consume(_ as Event)
	}
	
}
