package org.livingdoc.api.fixtures.decisiontables

import org.livingdoc.jvm.api.fixture.FixtureAnnotation
import org.livingdoc.api.Before
import org.livingdoc.api.After

/**
 * Annotating a class with this annotation declares it to be a fixture for a decision table example.
 *
 * **Decision Table:** *A table where each row represents a test case. A column can contain either a test input or an
 * expectation.*
 *
 * LivingDoc will evaluate the fixture class for consistency when it is first loaded. Only decision table related
 * annotations can be used within a decision table fixture!
 *
 * The parameter parallel allows the specification via the annotation, that the rows should be executed in parallel. The
 * default value of parallel is false, that means if not specified the rows will be executed serial.
 *
 * @see Before
 * @see After
 * @see BeforeRow
 * @see AfterRow
 * @see BeforeFirstCheck
 * @see Input
 * @see Check
 *
 * @since 2.0
 */
@Target(AnnotationTarget.CLASS)
@FixtureAnnotation
annotation class DecisionTableFixture(
    vararg val value: String = [""],
    val parallel: Boolean = false
)
