import groovy.lang.Script

import grails.plugins.hawkeventing.*
import grails.plugins.hawkeventing.config.*
import grails.plugins.hawkeventing.exceptions.*

import org.springframework.context.ApplicationContext
import org.springframework.util.ClassUtils

import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.commons.spring.GrailsApplicationContext
import org.codehaus.groovy.grails.plugins.DefaultGrailsPluginManager
import org.codehaus.groovy.grails.plugins.GrailsPlugin

class HawkEventingGrailsPlugin {

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
		
		// Event publishers
		syncEventPublisher(SyncEventPublisher)
		asyncEventPublisher(AsyncEventPublisher) { bean ->
			bean.autowire = 'byName'
		} 
		
		// Subscription and publishing
		eventBroker(EventBroker) {
			syncEventPublisher = ref("syncEventPublisher")
			asyncEventPublisher = ref("asyncEventPublisher")
		}
		
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
		
		// Get consumers from annotated Spring beans
		springBeanAnnotationConfigReader(SpringBeanAnnotationConfigurationReader) {
			eventBroker = ref("eventBroker")
		}
		
    }
	
    def doWithWebDescriptor = { xml -> }
	def doWithApplicationContext = { applicationContext -> }
	
	def onChange = { event -> }
	def onConfigChange = { event -> }
	
	def doWithDynamicMethods = { ctx -> }
	
}
