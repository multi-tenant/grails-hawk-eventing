package grails.plugins.hawkeventing.config

import grails.plugins.hawkeventing.EventBroker
import grails.plugins.hawkeventing.EventSubscription;
import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import spock.lang.*

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@TestMixin(GrailsUnitTestMixin)
class ScriptConfigurationReaderSpec extends Specification {

    def "extract consumer configuration from events dot groovy"() {
        given:
        ScriptConfigurationReader reader = Spy()
//        def  = new ScriptConfigurationReader()
        def broker = Mock(EventBroker)
        
        when: 
        def consumerConfig = reader.setEventBroker(broker)
        then: 
        1 * broker.subscribe(_ as Set)
    }
}