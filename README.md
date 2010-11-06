Hawk Eventing
===============

This is a plugin very similar to the [Falcone-Util plugin](http://grails.org/plugin/falcone-util), but without the Hibernate integration. 
The main goal of the plugin is to provide a very simple, non intrusive message broker.

Warning
--------

Expect the API to break.

Subscribing to events 
---------------------

### Manually, using the eventBroker bean

	class BookService implements InitializingBean {
		def eventBroker // Injected by Spring
		
		void afterPropertiesSet() {
			
			eventBroker.subscribe("hibernate.book") { Event event ->
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
	
### Implementing EventConsumer + registering it with the eventBroker

	class MyConsumer implements EventConsumer, InitializingBean {
	
		EventBroker eventBroker;
		
		void consume(Event event) {
			// ... 
		}
	
		void afterPropertiesSet() {
			eventBroker.subscribe("hibernate.saveOrUpdate.author", this);
		}
	
	}
	
### Using annotations

**Important:** This only works on Spring beans. 

	class BookService {
	
		@Consuming("hibernate.save.book")
		void onNewBook(Event newBookEvent) {
			// ...
		}
	
	}

Event bubbling
--------------

Events published to `hibernate.book.created` will also be published to `hibernate.book` and `hibernate`.


Changelog
----------

### v0.3 - November the 6th. 2010

* Feature: Support for declaring methods as event consumers using a @Consuming annotation.
* Refactoring: Moved configuration methods out from the plugin descriptor.
* Refactoring: Rewritten a lot of the classes in Java + more unit testing. 
* Breaking: Introduced the Event interface. 
Objects that's not an instance of Event will be wrapped in the BaseEvent class.
* Breaking: The EventBroker is not published together with the Event. 
This way events has the option to be serialized. 
Consumers in need of the broker should have it injected by Spring.   


Roadmap
--------

* Support for async events using the [Grails Executor](http://grails.org/plugin/executor) plugin?
* More testing and a few code refactorings. 