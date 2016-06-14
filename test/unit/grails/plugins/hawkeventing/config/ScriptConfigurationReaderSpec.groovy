package grails.plugins.hawkeventing.config

import grails.plugins.hawkeventing.EventBroker
import spock.lang.Specification

/**
 *
 * @author Kim A. Betti <kim.betti@gmail.com>
 */
class ScriptConfigurationReaderSpec extends Specification {

    def "extract consumer configuration from events dot groovy"() {
        given:
        def reader = new ScriptConfigurationReader()
        def broker = Mock(EventBroker)

        when:
        def consumerConfig = reader.setEventBroker(broker)
        then:
        1 * broker.subscribe(_ as Set)
    }
}
