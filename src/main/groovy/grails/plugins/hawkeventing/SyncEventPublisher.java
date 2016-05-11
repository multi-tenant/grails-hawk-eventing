package grails.plugins.hawkeventing;


/**
 * Synchronous event publisher.
 * @author Kim A. Betti
 */
public class SyncEventPublisher implements EventPublisher {

    @Override
    public void publish(Event event, EventConsumer consumer) {
        consumer.consume(event);
    }

}