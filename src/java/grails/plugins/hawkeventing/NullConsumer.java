package grails.plugins.hawkeventing;

/**
 * Event consumer that does nothing.
 * @author Kim A. Betti
 */
public class NullConsumer implements EventConsumer {

    @Override
    public void consume(Event event) {
    }

}