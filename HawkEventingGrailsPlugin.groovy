import groovy.lang.Script

import grails.plugins.hawkeventing.*
import grails.plugins.hawkeventing.annotation.HawkEventConsumer;
import grails.plugins.hawkeventing.config.*
import grails.plugins.hawkeventing.exceptions.*

import org.springframework.context.ApplicationContext
import org.springframework.util.ClassUtils

import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.commons.spring.GrailsApplicationContext
import org.codehaus.groovy.grails.plugins.DefaultGrailsPluginManager
import org.codehaus.groovy.grails.plugins.GrailsPlugin

class HawkEventingGrailsPlugin {

    def version = "0.5.1"

    def grailsVersion = "1.3.0 > *"
    def dependsOn = [:]

    def loadAfter = []

    def pluginExcludes = [
        "grails-app/views/error.gsp",
        "grails-app/conf/events.groovy",
        "grails-app/services/**",
    ]

    def author = "Kim A. Betti"
    def authorEmail = "kim@developer-b.com"

    def title = "Hawk Eventing"
    def documentation = "https://github.com/multi-tenant/grails-hawk-eventing"
    def description = "Very simple in-vm event publish / subscribe system."

    def doWithSpring = {

        syncEventPublisher(SyncEventPublisher)

        // Subscription and publishing
        eventBroker(EventBroker) {
            eventPublisher = ref("syncEventPublisher")
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
        consumerAnnotationReader(ConsumerAnnotationReader) {
            eventBroker = ref("eventBroker")
        }
    }

    def doWithApplicationContext = { ApplicationContext appCtx ->
        ConsumerAnnotationReader annotationReader = appCtx.consumerAnnotationReader
        appCtx.getBeansWithAnnotation(HawkEventConsumer).each { beanName, bean ->
            annotationReader.addConsumersFromClass(bean, beanName)
        }
    }

    def doWithWebDescriptor = { xml -> }
    def onChange = { event -> }
    def onConfigChange = { event -> }
    def doWithDynamicMethods = { ctx -> }
    
}
