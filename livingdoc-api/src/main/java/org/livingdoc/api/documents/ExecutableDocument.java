package org.livingdoc.api.documents;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.platform.commons.annotation.Testable;

/**
 * This annotation is used to mark a class as an executable document. The Living Doc engine looks for this annotation to
 * find the documents that should be executed. The annotated class can contain {@link org.livingdoc.api.fixtures.decisiontables.DecisionTableFixture}
 * and {@link org.livingdoc.api.fixtures.scenarios.ScenarioFixture}s. The executed contents are referenced as a parameter
 * of the annotation.
 *
 * An ExecutableDocument can be grouped by using the {@link Group} annotation.
 * An ExecutableDocument can be disabled by using the {@link org.livingdoc.api.disabled.Disabled} annotation.
 */
@Testable
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExecutableDocument {
    String value();

    /**
     * The {@link Group} that this ExecutableDocument belongs too.
     * <p>
     * This augments the usual lookup functionality, in which an ExecutableDocument that is declared nested inside a
     * class annotated with {@link Group} is considered part of that group.
     * </p>
     * <p>
     * Note that an ExecutableDocument can only belong to a single group. It is an error if the {@link Group} specified
     * by this attribute on the annotation and the {@link Group} discovered by the nested class lookup are different.
     * </p>
     *
     * @see Group
     */
    Class<?> group() default Object.class;

    Class<?>[] fixtureClasses() default {};
}
