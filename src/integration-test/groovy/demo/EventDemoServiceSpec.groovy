package demo

import spock.lang.Specification

//import grails.plugin.spock.IntegrationSpec
import grails.test.mixin.integration.Integration
import grails.transaction.*
import grails.plugins.hawkeventing.EventBroker
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.*

/**
 * 
 * @author Kim A. Betti
 */
@Integration
//@Rollback
class EventDemoServiceSpec extends Specification {

    @Autowired
    EventDemoService eventDemoService
    @Autowired
    EventBroker eventBroker

    def "consumers defined in annotations should be picked up"() {
        when: eventBroker.publish("book.created", "Groovy in action")
        then: eventDemoService.latestBook == "Groovy in action"
    }
}