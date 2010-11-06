import groovy.lang.Script

import grails.plugin.eventing.*
import grails.plugin.eventing.config.*
import grails.plugin.eventing.exceptions.*

import org.springframework.context.ApplicationContext
import org.springframework.util.ClassUtils

import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.commons.spring.GrailsApplicationContext
import org.codehaus.groovy.grails.plugins.DefaultGrailsPluginManager
import org.codehaus.groovy.grails.plugins.GrailsPlugin

class HawkEventingGrailsPlugin {

    def groupId = "plugins.utilities"	
    def version = "0.3"

    def grailsVersion = "1.3.0 > *"
    def dependsOn = [:]

    def pluginExcludes = [
		"grails-app/views/error.gsp",
		"grails-app/conf/events.groovy"
    ]

    def author = "Kim A. Betti"
    def authorEmail = "kim@developer-b.com"
	
    def title = "Eventing"
	def documentation = "http://grails.org/plugin/eventing"
	def description = 'Event plugin similar to the Falcone-Util plugin, but without the Hibernate integration.'
    
    def doWithSpring = {
		
		// Subscription and publishing
		eventBroker(EventBroker)
		
		// Reads subscriptions from events.groovy
		eventScriptConfigReader(ScriptConfigurationReader) {
			eventBroker = ref("eventBroker")
		}
		
		// Reads subscriptions from other plugins
		otherPluginConfigReader(OtherPluginsConfigurationReader) {
			eventBroker = ref("eventBroker")
			pluginManager = ref("pluginManager")
			grailsApplication = ref("grailsApplication")
		}
		
    }
	
    def doWithWebDescriptor = { xml -> }
	def doWithApplicationContext = { applicationContext -> }
	
	def onChange = { event -> }
	def onConfigChange = { event -> }
	
	def doWithDynamicMethods = { ctx -> }
	
}
