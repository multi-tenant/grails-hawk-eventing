Grails Eventing
===============

This is a plugin very similar to the [Falcone-Util plugin](http://grails.org/plugin/falcone-util), but without the Hibernate integration. 
The main goal of the plugin is to provide a very simple, non intrucive message broker.

Warning
--------

I've just started hacking at this so expect the api to break.

Subscribing to events 
---------------------

### Manually, using the eventBroker bean

	class BookService implements InitializingBean {
		def eventBroker // Injected by Spring
		
		void afterPropertiesSet() {
			
			eventBroker.subscribe("hibernate.book") { Book book ->
				// Do something with book
			}
			
		}
		
	}

### In applications, creating grails-app/conf/events.groovy

	consumers = {
	
		hibernate.book.created { Book book -> 
			// Do something with book
		}
		
	}

### In plugins, adding a doWithEvents closure to the plugin description file (...GrailsPlugin.groovy)

	def doWithEvents = { ApplicationContext ctx ->
	
		hibernate.book.created { Book book -> 
			// Do something with book
		}
		
	}
	
### Using annotations? (Not implemented)

I'd like to implement this feature. 

	class BookService {
	
		@Consuming("hibernate.book.created")
		void onNewBook(Book book) {
		
		}
	
	}

Event bubbling
--------------

Events published to `hibernate.book.created` will also be published to `hibernate.book` and `hibernate`.

**Weakness:** The consumer has currently no way of accessing the original event name. 
