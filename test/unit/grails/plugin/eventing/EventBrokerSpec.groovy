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
	}
	
	def "subscribe from closure"() {
		given:
		def cfg = ConsumerBuilder.fromClosure {
			book.created { String nameOfBook -> }
		}
		
		when: broker.addSubscriptionsFromConfiguration(cfg)
		then: broker.getEventConsumers('book.created').size() == 1
	}
	
	def "unsubscribe"() {
		given:
		def cfg = ConsumerBuilder.fromClosure {
			book.created { String nameOfBook -> }
		}
		
		when:
		broker.addSubscriptionsFromConfiguration(cfg)
		Set consumers = broker.getEventConsumers('book.created')
		
		then: 
		consumers.size() == 1
		
		when:
		broker.unsubscribe('book.created', consumers.toArray()[0])
		consumers = broker.getEventConsumers('book.created')
		
		then: 
		consumers.size() == 0
	}
	
	def "Event recieved by single consumer"() {
		given:
		def consumer = Mock(EventConsumer)
		
		when:
		broker.subscribe("book.created", consumer)
		broker.publish("book.created", "Groovy in action")
		
		then:
		1 * consumer.consume("Groovy in action", broker)
	}
	
	def "Events are only recieved by the expected consumers"() {
		given:
		def bookConsumer = Mock(EventConsumer)
		def secondBookConsumer = Mock(EventConsumer)
		def authorConsumer = Mock(EventConsumer)
		
		when:
		broker.subscribe("book.created", bookConsumer)
		broker.subscribe("book.created", secondBookConsumer)
		broker.subscribe("author.created", authorConsumer)
		broker.publish("book.created", "Groovy in action")
		
		then:
		1 * bookConsumer.consume("Groovy in action", broker)
		1 * secondBookConsumer.consume("Groovy in action", broker)
		0 * authorConsumer.consume("Groovy in action", broker)
	}
	
	def "Event bubbling"() {
		given:
		def hibernateConsumer = Mock(EventConsumer)
		def bookConsumer = Mock(EventConsumer)
		def bookCreatedConsumer = Mock(EventConsumer)
		def bookDeletedConsumer = Mock(EventConsumer)
		
		when:
		broker.subscribe("hibernate", hibernateConsumer)
		broker.subscribe("hibernate.book", bookConsumer)
		broker.subscribe("hibernate.book.created", bookCreatedConsumer)
		broker.subscribe("hibernate.book.deleted", bookDeletedConsumer)
		broker.publish("hibernate.book.created", "Groovy in action")
		
		then:
		1 * hibernateConsumer.consume("Groovy in action", broker)
		1 * bookConsumer.consume("Groovy in action", broker)
		1 * bookCreatedConsumer.consume("Groovy in action", broker)
		0 * bookDeletedConsumer.consume("Groovy in action", broker)
	}
	
}
