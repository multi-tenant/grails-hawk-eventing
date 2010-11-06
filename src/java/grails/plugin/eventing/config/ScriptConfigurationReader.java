package grails.plugin.eventing.config;

import grails.plugin.eventing.*;
import grails.plugin.eventing.exceptions.*;
import groovy.lang.Closure;
import groovy.lang.MissingPropertyException;
import groovy.lang.Script;

import java.util.Set;

/**
 * Reads configuration from a groovy script. 
 * The script should contain a 'consumers' closure. 
 * 
 * @author Kim A. Betti 
 */
class ScriptConfigurationReader {

	public final String CONFIG_SCRIPT_NAME = "events";
	public final String CONFIG_CLOSURE_PROPERTY_NAME = "consumers";
	
	void setEventBroker(EventBroker eventBroker) {
		try {
			Class<?> configClass = getConfigClass();
			Closure configClosure = extractConfigClosure(configClass);
			Set<EventSubscription> subscriptions = ClosureSubscriptionFactory.fromClosure(configClosure).getSubscriptions();
			registerSubscriptions(subscriptions, eventBroker);
		} catch (ClassNotFoundException ex) {
			// Perfectly okay, applications doesn't have
			// to provide events.groovy
		} catch (MissingPropertyException ex) {
			missingConfigurationClosure();
		} catch (Exception ex) {
			otherException(ex);
		}
	}
	
	private Class<?> getConfigClass() throws ClassNotFoundException {
		ClassLoader classLoader = this.getClass().getClassLoader();
		return Class.forName(CONFIG_SCRIPT_NAME, false, classLoader);
	}
	
	private Closure extractConfigClosure(Class<?> configClass) throws Exception {
		Script script = (Script) configClass.newInstance();
		script.run();
		return (Closure) script.getProperty(CONFIG_CLOSURE_PROPERTY_NAME);
	}
	
	private void missingConfigurationClosure() {
		String message = "events.groovy should contain a " 
					    + CONFIG_CLOSURE_PROPERTY_NAME + " closure";
		
		throw new InvalidEventConfigurationException(message);
	}
	
	private void otherException(Exception ex) {
		String message = "Exception when reading consumers from "
						+ CONFIG_SCRIPT_NAME + ".groovy: " + ex.getMessage();
		
		throw new InvalidEventConfigurationException(message);
	}
	
	private void registerSubscriptions(Set<EventSubscription> subscriptions, EventBroker eventBroker) {
		for (EventSubscription subscription : subscriptions) 
			eventBroker.subscribe(subscription);
	}
	
}
