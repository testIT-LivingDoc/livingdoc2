package org.livingdoc.api;

import org.livingdoc.api.documents.ExecutableDocument;
import org.livingdoc.api.fixtures.scenarios.ScenarioFixture;
import org.livingdoc.api.fixtures.scenarios.Step;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Methods annotated with this annotation are invoked before the first fixture of an {@link ExecutableDocument} or
 * before the first scenario {@link Step} method is invoked.
 * <p>
 * <b>Constraints:</b>
 * <ol>
 * <li>The annotated method must not have any parameters!</li>
 * <li>If multiple methods of a single fixture are annotated the invocation order is non-deterministic!</li>
 * </ol>
 *
 * @see ExecutableDocument
 * @see Step
 * @see ScenarioFixture
 * @since 2.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Before {
}
