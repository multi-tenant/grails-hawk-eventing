package grails.plugins.hawkeventing;

/**
 * Implemented by all supported event publishers.
 *
 * @author Kim A. Betti
 */
interface EventPublisher {

    void publish(Event event, EventConsumer consumer);

}