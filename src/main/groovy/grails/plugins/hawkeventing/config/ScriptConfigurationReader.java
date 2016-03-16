package grails.plugins.hawkeventing.config;

import grails.core.GrailsApplication;
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

//    EventBroker eventBroker;
    
    public void setEventBroker(EventBroker eventBroker) {
        try {
//            System.out.println("getting config");
            Class<?> configClass = getConfigClass();
//            System.out.println("Got config class = "+configClass);
            Closure configClosure = extractConfigClosure(configClass);
            Set<EventSubscription> subscriptions = ClosureSubscriptionFactory.fromClosure(configClosure).getSubscriptions();
//            System.out.println("got subscriptions");
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
//        System.out.println("getting grails app");
        GrailsApplication grailsApplication = grails.util.Holders.getGrailsApplication();
//        System.out.println("got grails app = ${grailsApplication}");
        ClassLoader classLoader = grailsApplication.getClassLoader();
//        System.out.println("getting class loader");
//        System.out.println("got class loader ${classLoader}");
//        Class<?> result =  grailsApplication.getClassForName(CONFIG_SCRIPT_NAME);
      Class<?> result = Class.forName(CONFIG_SCRIPT_NAME, true, classLoader);
        return result;
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
