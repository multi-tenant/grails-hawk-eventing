package grails.plugins.hawkeventing;

/**
 * An event consumer can be implemented in various ways.
 * Closures, reflection, jms.. you name it!
 * @author Kim A. Betti
 */
public interface EventConsumer {

	void consume(Event event);
	
}