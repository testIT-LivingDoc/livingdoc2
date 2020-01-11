package org.livingdoc.api.documents;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.platform.commons.annotation.Testable;


@Testable
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExecutableDocument {
    String value();

    /**
     * The {@link Group} that this ExecutableDocument belongs too.
     * <p>
     * Note that if this class is nested inside a group it is an error to specify this attribute to be different from
     * the parent class
     *
     * @see Group
     */
    Class<?> group() default Object.class;

    Class<?>[] fixtureClasses() default {};
}
