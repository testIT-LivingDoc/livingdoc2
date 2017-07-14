package org.livingdoc.api.fixtures.decisiontables;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Methods annotated with this annotation are invoked before the first {@link Check} is executed. These methods are
 * generally used to execute test code based on the {@link Input} values and store the results to be evaluated by the {@link
 * Check} methods. This is a purely optional mechanic! Depending on the test case, directly executing that logic inside a
 * {@link Check} method might be the best approach.
 * <p>
 * <b>Constraints:</b>
 * <ol>
 * <li>The annotated method must not have any parameters!</li>
 * <li>If multiple methods of a single fixture are annotated the invocation order is non-deterministic!</li>
 * </ol>
 *
 * @see DecisionTableFixture
 * @see Check
 * @since 2.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface BeforeFirstCheck {
}
