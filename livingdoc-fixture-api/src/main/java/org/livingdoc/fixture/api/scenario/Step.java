package org.livingdoc.fixture.api.scenario;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Methods annotation with this annotation are considered as candidates for steps of a scenario example (see {@link
 * ScenarioFixture}).
 * <p>
 * TODO: describe constraints and format of {@link #value()}.
 *
 * @see ScenarioFixture
 * @since 2.0
 */
@Repeatable(Step.Steps.class)
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Step {

    String[] value();

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface Steps {
        Step[] value();
    }

}
