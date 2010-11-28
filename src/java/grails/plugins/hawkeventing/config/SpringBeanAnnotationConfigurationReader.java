package grails.plugins.hawkeventing.config;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import grails.plugins.hawkeventing.EventBroker;
import grails.plugins.hawkeventing.EventConsumer;
import grails.plugins.hawkeventing.MethodEventConsumer;
import grails.plugins.hawkeventing.annotation.Consuming;

/**
 * Looks at all Spring beans for @Consuming annotations
 * @author Kim A. Betti
 */
public class SpringBeanAnnotationConfigurationReader implements BeanPostProcessor {

	private static final Log log = LogFactory.getLog(SpringBeanAnnotationConfigurationReader.class);
	
	private EventBroker eventBroker;

	public void setEventBroker(EventBroker eventBroker) {
		this.eventBroker = eventBroker;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		for (Method consumerMethod : getConsumerMethods(bean.getClass())) {
			EventConsumer eventConsumer = new MethodEventConsumer(bean, consumerMethod);
			for (String eventName : getEventNames(consumerMethod)) {
				log.info("Subscribing " + eventName + " to " + beanName + ", method " + consumerMethod.getName());
				eventBroker.subscribe(eventName, eventConsumer);
			}
		}
		
		return bean;
	}

	private Set<Method> getConsumerMethods(Class<?> beanClass) {
		Set<Method> consumerMethods = new HashSet<Method>();
		for (Method method : beanClass.getMethods()) 
			if (method.isAnnotationPresent(Consuming.class)) 
				consumerMethods.add(method);
		
		return consumerMethods;
	}
	
	private String[] getEventNames(Method method) {
		Consuming consumingAnnotation = method.getAnnotation(Consuming.class);
		return consumingAnnotation.value();
	}
	
	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}
	
}
