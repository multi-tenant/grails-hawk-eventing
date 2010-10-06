package grails.plugin.eventing.exceptions;

@SuppressWarnings("serial")
public class InvalidEventConfigurationException extends RuntimeException {

	public InvalidEventConfigurationException(String message) {
		super(message);
	}
	
	public InvalidEventConfigurationException(String message, Exception ex) {
		super(message, ex);
	}

}
