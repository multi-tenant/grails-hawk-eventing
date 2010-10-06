import groovy.lang.Script

import grails.plugin.eventing.*
import grails.plugin.eventing.exceptions.*

import org.springframework.context.ApplicationContext
import org.springframework.util.ClassUtils

import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.commons.spring.GrailsApplicationContext
import org.codehaus.groovy.grails.plugins.DefaultGrailsPluginManager
import org.codehaus.groovy.grails.plugins.GrailsPlugin

class EventingGrailsPlugin {
	
    def version = "0.1"
    def grailsVersion = "1.3.0 > *"
    def dependsOn = [:]

    def pluginExcludes = [
		"grails-app/views/error.gsp"
    ]

    def author = "Multi Tenant Interest Group"
    def authorEmail = "..."
	
    def title = "Eventing"
	def documentation = "http://grails.org/plugin/eventing"
	def description = 'Event plugin similar to the Falcone-Util plugin, but without the Hibernate integration.'
    
    def doWithSpring = {
		eventBroker(EventBroker)
    }
	
	def doWithDynamicMethods = { ctx -> 
		def eventBroker = ctx.getBean("eventBroker")
		addSubscriptionsFromOtherPlugins(eventBroker, manager, application)
		addSubscriptionsFromGroovyFiles(eventBroker, application)
	}
	
	void addSubscriptionsFromOtherPlugins(EventBroker broker, DefaultGrailsPluginManager pluginManager, GrailsApplication application) {
		pluginManager.allPlugins.each { GrailsPlugin plugin ->
			def pluginPropeties = plugin.instance.properties
			if (pluginPropeties.containsKey("doWithEvents")) {
				def cfgClosure = pluginPropeties.get("doWithEvents")
				def cfg = ConsumerBuilder.fromClosure(cfgClosure, application)
				broker.addSubscriptionsFromConfiguration(cfg)
			}
		}
	}
	
	void addSubscriptionsFromGroovyFiles(EventBroker broker, GrailsApplication application) {
		def cfgClosure = getConfigClosureFromClass("events", application)
		def configuration = ConsumerBuilder.fromClosure(cfgClosure)
		broker.addSubscriptionsFromConfiguration(configuration)
	}
	
	Closure getConfigClosureFromClass(String configClassName, GrailsApplication application) {
		Closure configClosure
		
		try {
			Class configClass = ClassUtils.forName(configClassName, application.classLoader);
			Script script = (Script) configClass.newInstance()
			script.run()
			configClosure = script.getProperty("consumers")
		} catch (MissingPropertyException ex) {
			String msg = "Expected to find a 'consumers' property in events.groovy"
			throw new InvalidEventConfigurationException(msg)
		} catch (ClassNotFoundException ex) {
			// No big deal, it just means that the
			// application doesn't contain a events.groovy.
		} catch (Exception ex) {
			String msg = "Unable to read configuration from events.groovy, " + ex.message
			throw new InvalidEventConfigurationException(msg, ex)
		}
	
		return configClosure ?: { }
	}
	
    def doWithWebDescriptor = { xml -> }
	def doWithApplicationContext = { applicationContext -> }
	
	def onChange = { event -> }
	def onConfigChange = { event -> }
	
}
