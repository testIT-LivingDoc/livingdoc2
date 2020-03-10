package org.livingdoc.api.fixtures.scenarios

import org.livingdoc.jvm.api.fixture.FixtureAnnotation
import org.livingdoc.api.Before
import org.livingdoc.api.After

/**
 * Annotating a class with this annotation declares it to be a fixture for a scenario example.
 *
 * **Scenario:** *An example describing a single test case as a sequence of steps. Each step is represented as a
 * sentence containing either one or more test inputs or one or more expectations.*
 *
 * LivingDoc will evaluate the fixture class for consistency when it is first loaded. Only scenario related annotations
 * can be used within a scenario fixture!
 *
 * @see Before
 * @see After
 * @see Step
 *
 * @since 2.0
 */
@Target(AnnotationTarget.CLASS)
@FixtureAnnotation
annotation class ScenarioFixture(vararg val value: String)
