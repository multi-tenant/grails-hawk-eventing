package grails.plugins.hawkeventing.config

import grails.plugins.hawkeventing.EventBroker;
import grails.plugins.hawkeventing.EventSubscription;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.groovy.grails.commons.GrailsApplication;
import org.codehaus.groovy.grails.plugins.GrailsPlugin;
import org.codehaus.groovy.grails.plugins.GrailsPluginManager;
import org.springframework.beans.factory.InitializingBean;

/**
 * Generates subscriptions from plugins providing
 * a doWithEvents closure in their plugin descriptor. 
 * 
 * @author Kim A. Betti
 */
class OtherPluginsConfigurationReader implements InitializingBean {
	
	private static final Log log = LogFactory.getLog(this)

	public static final String EVENT_CLOSURE_PROPERTY_NAME = "doWithEvents"
	
	EventBroker eventBroker
	GrailsApplication grailsApplication
	GrailsPluginManager pluginManager
	
	void afterPropertiesSet() {
		log.debug "Reading event configuration from installed plugins"
		pluginManager.allPlugins.each { GrailsPlugin plugin ->
			def pluginPropeties = plugin.instance.properties
			if (pluginPropeties.containsKey("doWithEvents")) {
				log.debug "Found event configuration in " + plugin.getName()
				Closure configClosure = pluginPropeties.get(EVENT_CLOSURE_PROPERTY_NAME)
				Set<EventSubscription> subscriptions = ClosureSubscriptionFactory.fromClosure(configClosure).getSubscriptions();
				eventBroker.subscribe(subscriptions);
			}
		}
	}
	
}
