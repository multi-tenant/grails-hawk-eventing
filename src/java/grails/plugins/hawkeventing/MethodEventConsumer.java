package grails.plugins.hawkeventing;

import grails.plugins.hawkeventing.exceptions.EventException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Support publishing of events to specific method.
 * This is most likely configured by annotations.
 * 
 * Note! It makes use of the reflection API so there
 * might be a slight performance trade of involved
 * with using this.
 * 
 * @author Kim A. Betti
 */
public class MethodEventConsumer implements EventConsumer {

    private final Method method;
    private final Object consumer;

    public MethodEventConsumer(Object consumer, Method method) {
        this.consumer = consumer;
        this.method = method;
    }

    @Override
    public void consume(Event event) {
        try {
            method.invoke(consumer, event);
        } catch (IllegalAccessException ex) {
            onReflectionException(ex, event);
        } catch (IllegalArgumentException ex) {
            onReflectionException(ex, event);
        } catch (InvocationTargetException ex) {
            onReflectionException(ex, event);
        }
    }

    private void onReflectionException(Exception ex, Event event) {
        String exMessage = "Unable to notify " + consumer + " of "
        + event.getEventName() + " because of reflection related exception";
        throw new EventException(exMessage, ex);
    }

}