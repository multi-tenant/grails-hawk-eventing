package grails.plugin.eventing.config

import grails.plugin.eventing.*
import grails.plugin.spock.UnitSpec

/**
 * 
 * @author Kim A. Betti <kim.betti@gmail.com>
 */
class ScriptConfigurationReaderSpec extends UnitSpec {

	def "extract consumer configuration from events dot groovy"() {
		given:
			def reader = new ScriptConfigurationReader()
			def broker = Mock(EventBroker)
			
		when:
			def consumerConfig = reader.setEventBroker(broker)
			
		then:
		2 * broker.subscribe(_ as EventSubscription)
	}
	
}
