package org.livingdoc.api.fixtures.scenarios;

import org.livingdoc.api.After;
import org.livingdoc.api.Before;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Annotating a class with this annotation declares it to be a fixture for a scenario example.
 * <p>
 * <b>Scenario:</b> <i>An example describing a single test case as a sequence of steps. Each step is represented as a
 * sentence containing either one or more test inputs or one or more expectations.</i>
 * <p>
 * LivingDoc will evaluate the fixture class for consistency when it is first loaded. Only scenario related annotations can
 * be used within a scenario fixture!
 *
 * @see Before
 * @see After
 * @see Step
 * @since 2.0
 */
@Repeatable(ScenarioFixture.ScenarioFixtures.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ScenarioFixture {

    String[] value();

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @interface ScenarioFixtures {
        ScenarioFixture[] value();
    }

}
