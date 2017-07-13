package org.livingdoc.api.fixtures.scenarios;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Binds a name to the annotated parameter. If the bound name occurs as parameter in a {@link Step}
 * annotation of this method, the extracted value will be converted and passed to this method during
 * execution.
 *
 * @see Step
 * @see ScenarioFixture
 * @since 2.0
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Binding {
    String value();
}
