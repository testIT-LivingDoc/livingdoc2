package org.livingdoc.api.disabled;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Disabled is used to signal that the annotated test class is currently disabled and should not be
 * executed.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Disabled {

    String value() default "";

}
