package grails.plugins.hawkeventing.exceptions;

@SuppressWarnings("serial")
public class EventException extends RuntimeException {

    public EventException(String message) {
        super(message);
    }

    public EventException(String message, Exception ex) {
        super(message, ex);
    }

}
