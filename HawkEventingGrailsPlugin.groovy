import grails.plugins.hawkeventing.EventBroker
import grails.plugins.hawkeventing.SyncEventPublisher
import grails.plugins.hawkeventing.annotation.HawkEventConsumer
import grails.plugins.hawkeventing.config.ConsumerAnnotationReader
import grails.plugins.hawkeventing.config.OtherPluginsConfigurationReader
import grails.plugins.hawkeventing.config.ScriptConfigurationReader
import org.springframework.context.ApplicationContext

class HawkEventingGrailsPlugin {
    // the plugin version
    def version = "1.0"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "2.4 > *"
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
            "grails-app/views/error.gsp",
            "grails-app/conf/events.groovy",
            "grails-app/services/**",
    ]

    def title = "Hawk Eventing" // Headline display name of the plugin
    def author = "Sandeep Poonia"
    def authorEmail = "sandeep.poonia.90@gmail.com"
    def description = ' Very simple in-vm event publish / subscribe system.'

    // URL to the plugin's documentation
    def documentation = "https://github.com/spoonia/grails-hawk-eventing"

    // Extra (optional) plugin metadata

    // License: one of 'APACHE', 'GPL2', 'GPL3'
    def license = "APACHE"

    // Details of company behind the plugin (if there is one)
//    def organization = [name: "My Company", url: "http://www.my-company.com/"]

    // Any additional developers beyond the author specified above.
//    def developers = [[name: "Joe Bloggs", email: "joe@bloggs.net"]]

    // Location of the plugin's issue tracker.
    def issueManagement = [system: 'GitHub', url: 'https://github.com/spoonia/grails-hawk-eventing/issues']

    // Online location of the plugin's browseable source code.
    def scm = [url: 'https://github.com/spoonia/grails-hawk-eventing']

    def doWithWebDescriptor = { xml ->
        // TODO Implement additions to web.xml (optional), this event occurs before
    }

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

    def doWithDynamicMethods = { ApplicationContext ctx ->
        // TODO Implement registering dynamic methods to classes (optional)
    }

    def doWithApplicationContext = { ApplicationContext ctx ->
        ConsumerAnnotationReader annotationReader = ctx.consumerAnnotationReader
        ctx.getBeansWithAnnotation(HawkEventConsumer).each { beanName, bean ->
            annotationReader.addConsumersFromClass(bean, beanName)
        }
    }

    def onChange = { event ->
        // TODO Implement code that is executed when any artefact that this plugin is
        // watching is modified and reloaded. The event contains: event.source,
        // event.application, event.manager, event.ctx, and event.plugin.
    }

    def onConfigChange = { event ->
        // TODO Implement code that is executed when the project configuration changes.
        // The event is the same as for 'onChange'.
    }

    def onShutdown = { event ->
        // TODO Implement code that is executed when the application shuts down (optional)
    }
}
