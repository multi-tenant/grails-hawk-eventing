package grails.plugins.hawkeventing

import grails.plugins.hawkeventing.exceptions.InvalidEventConfigurationException

/**
 * See tests for usage.
 * TODO: give a better name
 * 
 * @author Kim A. Betti <kim.betti@gmail.com>
 */
class ClosureSubscriptionFactory implements EventConsumerConfiguration {

    protected Closure configurationClosure
    protected Object closureArgument
    protected Set<EventSubscription> subscriptions = []
    protected List currentEventHierarchi = []

    protected ClosureSubscriptionFactory(Closure configurationClosure, Object ctx) {
        this.configurationClosure = configurationClosure
        this.closureArgument = ctx
    }

    public static ClosureSubscriptionFactory fromClosure(Closure configurationClosure, Object ctx = null) {
        return new ClosureSubscriptionFactory(configurationClosure, ctx)
    }

    @Override
    public Set<EventSubscription> getSubscriptions() {
        invokeConfgurationClosure()
        return this.subscriptions
    }

    private void invokeConfgurationClosure(Closure cfgClosure) {
        configurationClosure.delegate = this
        configurationClosure.call(closureArgument)
    }

    void methodMissing(String name, Object arguments) {
        String fullEventName = getFullEventName(name)
        addEventConsumer(fullEventName, (Object[]) arguments)
    }

    private String getFullEventName(String lastName) {
        currentEventHierarchi << lastName
        String fullName = currentEventHierarchi.join('.')
        currentEventHierarchi.clear()
        return fullName
    }

    private void addEventConsumer(String fullEventName, Object[] arguments) {
        hasAtLeastOneArgument(arguments)
        Closure eventHandler = getEventHandler(arguments)
        String consumerName = getConsumerName(arguments)
        EventConsumer eventConsumer = new ClosureEventConsumer(eventHandler, consumerName)
        subscriptions << new EventSubscription(fullEventName, eventConsumer)
    }

    private void hasAtLeastOneArgument(Object[] arguments) {
        if (arguments.length == 0) {
            String msg = "No arguments supplied for event $eventName, expected (at least) a closure"
            throw new InvalidEventConfigurationException(msg)
        }
    }

    private Closure getEventHandler(Object[] arguments) {
        Object lastArgument = arguments[arguments.length-1]
        if (!isClosure(lastArgument)) {
            String msg = "Expected the last argument to be a closure, not " + lastArgument.getClass().name
            throw new InvalidEventConfigurationException(msg)
        }

        return lastArgument
    }

    private String getConsumerName(Object[] arguments) {
        String consumerName = "anonymous"
        if (arguments.length > 1 && arguments[0] instanceof String) {
            consumerName = arguments[0]
            if (consumerName.length() == 0) {
                String msg = "Event consumer name can't be empty"
                throw new InvalidEventConfigurationException(msg)
            }
        }

        return consumerName
    }

    private boolean isClosure(Object argument) {
        return argument instanceof Closure
    }

    def propertyMissing(String name) {
        currentEventHierarchi << name
        return this
    }
}
