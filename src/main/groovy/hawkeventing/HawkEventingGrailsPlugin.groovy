package hawkeventing

import grails.plugins.*
import grails.plugins.hawkeventing.EventBroker
import grails.plugins.hawkeventing.SyncEventPublisher
import grails.plugins.hawkeventing.annotation.HawkEventConsumer
import grails.plugins.hawkeventing.config.ConsumerAnnotationReader
import grails.plugins.hawkeventing.config.OtherPluginsConfigurationReader
import grails.plugins.hawkeventing.config.ScriptConfigurationReader

import org.springframework.context.ApplicationContext

class HawkEventingGrailsPlugin extends Plugin {

    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "3.0.1 > *"
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
        "grails-app/conf/events.groovy",
        "grails-app/services/**",
    ]

    def author = "Kim A. Betti"
    def authorEmail = "kim@developer-b.com"

    def title = "Hawk Eventing"
    def documentation = "https://github.com/multi-tenant/grails-hawk-eventing"
    def description = "Very simple in-vm event publish / subscribe system."

    def license = "APACHE"
//    def developers = [[name: "Joe Bloggs", email: "joe@bloggs.net"]]
    def issueManagement = [system: 'GitHub', url: 'https://github.com/multi-tenant/grails-hawk-eventing/issues']
    def scm = [url: 'https://github.com/multi-tenant/grails-hawk-eventing']

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
}
