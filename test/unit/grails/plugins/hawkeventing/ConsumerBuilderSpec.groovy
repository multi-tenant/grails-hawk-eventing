package grails.plugins.hawkeventing

import grails.plugins.hawkeventing.exceptions.InvalidEventConfigurationException
import spock.lang.Specification

/**
 * @author Kim A. Betti <kim.betti@gmail.com>
 */
class ConsumerBuilderSpec extends Specification {

    def "Single event definition"() {
        when:
        Set subscriptions = ClosureSubscriptionFactory.fromClosure {
            hibernate.book.delete { book -> }
        }.getSubscriptions()

        then: "Found one consumer with the expected name"
        subscriptions.size() == 1
        shouldSubscribeTo(subscriptions, ["hibernate.book.delete"])
    }

    def "Multiple listeners for single event"() {
        when:
        Set subscriptions = ClosureSubscriptionFactory.fromClosure {
            hibernate.book.delete { book -> }
            hibernate.book.delete { book -> }
        }.getSubscriptions()

        then: "Found one consumer with the expected name"
        subscriptions.size() == 2
        shouldSubscribeTo(subscriptions, ["hibernate.book.delete"])
    }

    def "Multiple event definitions for different events"() {
        when:
        Set subscriptions = ClosureSubscriptionFactory.fromClosure {
            hibernate.book.delete { book -> }
            hibernate.sessionCreated { session -> }
            customEvent {}
        }.getSubscriptions()

        then: "Three consumers are added"
        subscriptions.size() == 3

        and: "With the correct event names"
        shouldSubscribeTo(subscriptions, [
                "hibernate.book.delete",
                "hibernate.sessionCreated",
                "customEvent"
        ])
    }

    def "Event definition with name"() {
        when:
        Set subscriptions = ClosureSubscriptionFactory.fromClosure {
            hibernate.book.delete "remove-from-library", { book -> }
        }.getSubscriptions()

        then: "The consumers has the expected names"
        subscriptions.toList().get(0).consumer.consumerName == "remove-from-library"
    }

    def "Event definitions without name are named anonymous"() {
        when:
        Set subscriptions = ClosureSubscriptionFactory.fromClosure {
            hibernate.book.delete { book -> }
        }.getSubscriptions()

        then: "The consumer is named anonymous"
        shouldSubscribeTo(subscriptions, ["hibernate.book.delete"])
        subscriptions.toList().get(0).consumer.consumerName == "anonymous"
    }

    def "Empty name triggers exception"() {
        when:
        Set subscriptions = ClosureSubscriptionFactory.fromClosure {
            hibernate.book.delete "", { book -> }
        }.getSubscriptions()

        then: "An exception is thrown"
        thrown(InvalidEventConfigurationException)
    }

    private void shouldSubscribeTo(Set subscriptions, List eventNames) {
        for (String eventName : eventNames) {
            boolean found = false
            for (EventSubscription subscription : subscriptions) {
                if (subscription.eventName == eventName) {
                    found = true
                    break
                }
            }

            assert found, "Expected a subscription to " + eventName
        }
    }
}
