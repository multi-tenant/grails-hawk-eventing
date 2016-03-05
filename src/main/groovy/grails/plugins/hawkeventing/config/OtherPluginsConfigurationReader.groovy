package grails.plugins.hawkeventing.config

import grails.plugins.hawkeventing.ClosureSubscriptionFactory
import grails.plugins.hawkeventing.EventBroker
import grails.plugins.hawkeventing.EventSubscription

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import grails.core.GrailsApplication
import grails.plugins.GrailsPlugin
import grails.plugins.GrailsPluginManager

import org.springframework.beans.factory.config.RuntimeBeanReference
import org.springframework.beans.factory.InitializingBean

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
		pluginManager.getAllPlugins().each { GrailsPlugin plugin ->
			def pluginPropeties = plugin.getInstance().properties
			if (pluginPropeties.containsKey("doWithEvents")) {
				log.debug "Found event configuration in $plugin.name"
				Closure configClosure = pluginPropeties.get(EVENT_CLOSURE_PROPERTY_NAME)
				Set<EventSubscription> subscriptions = ClosureSubscriptionFactory.fromClosure(configClosure).getSubscriptions()
				eventBroker.subscribe(subscriptions)
			}
		}
	}
}
