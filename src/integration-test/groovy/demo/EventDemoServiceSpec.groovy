package demo

import grails.plugin.spock.IntegrationSpec

/**
 * 
 * @author Kim A. Betti
 */
class EventDemoServiceSpec extends IntegrationSpec {

    def eventDemoService
    def eventBroker

    def "consumers defined in annotations should be picked up"() {
        when: eventBroker.publish("book.created", "Groovy in action")
        then: eventDemoService.latestBook == "Groovy in action"
    }
}