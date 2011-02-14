package grails.plugins.hawkeventing.config;

import grails.plugins.hawkeventing.ClosureSubscriptionFactory;
import grails.plugins.hawkeventing.EventBroker;
import grails.plugins.hawkeventing.EventSubscription;
import grails.plugins.hawkeventing.exceptions.InvalidEventConfigurationException;
import groovy.lang.Closure;
import groovy.lang.MissingPropertyException;
import groovy.lang.Script;

import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Reads configuration from a groovy script. The script should contain a
 * 'consumers' closure.
 * 
 * @author Kim A. Betti
 */
class ScriptConfigurationReader {

    public final String CONFIG_SCRIPT_NAME = "events";
    public final String CONFIG_CLOSURE_PROPERTY_NAME = "consumers";

    private static final Log log = LogFactory.getLog(ScriptConfigurationReader.class);

    public void setEventBroker(EventBroker eventBroker) {
        try {
            Class<?> configClass = getConfigClass();
            Closure configClosure = extractConfigClosure(configClass);
            Set<EventSubscription> subscriptions = ClosureSubscriptionFactory.fromClosure(configClosure).getSubscriptions();
            eventBroker.subscribe(subscriptions);
        } catch (ClassNotFoundException ex) {
            missingConfigScript();
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

    /**
     * Perfectly okay, applications doesn't have to provide events.groovy.
     */
    private void missingConfigScript() {
        log.info("Did not detect events.groovy");
    }

    private void missingConfigurationClosure() {
        String message = "events.groovy should contain a " + CONFIG_CLOSURE_PROPERTY_NAME + " closure";

        throw new InvalidEventConfigurationException(message);
    }

    private void otherException(Exception ex) {
        String message = "Exception when reading consumers from " + CONFIG_SCRIPT_NAME + ".groovy: " + ex.getMessage();

        throw new InvalidEventConfigurationException(message, ex);
    }

}
