package org.livingdoc.fixture.api.decisiontable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Methods annotated with this annotation are invoked once for each row of the decision table example just before it is
 * evaluated. These methods are intended to setup resources needed for the execution of a single row.
 * <p>
 * <b>Constraints:</b>
 * <ol>
 * <li>The annotated method must not have any parameters!</li>
 * <li>If multiple methods of a single fixture are annotated the invocation order is non-deterministic!</li>
 * </ol>
 *
 * @see DecisionTableFixture
 * @since 2.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface BeforeRow {
}
