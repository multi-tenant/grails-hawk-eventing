package grails.plugin.eventing;

/**
 * 
 * @author Kim A. Betti
 */
public class NullConsumer implements EventConsumer {

	@Override
	public void consume(Event event) {
	}
	
}
