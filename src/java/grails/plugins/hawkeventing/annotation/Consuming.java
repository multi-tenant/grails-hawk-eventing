package grails.plugins.hawkeventing.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotate the class containing tenant information with this. This annotation
 * should only be applied to one domain class per project.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Consuming {
    String[] value();
}
