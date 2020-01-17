package org.livingdoc.api.fixtures.decisiontables;

import org.livingdoc.api.After;
import org.livingdoc.api.Before;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Annotating a class with this annotation declares it to be a fixture for a decision table example.
 * <p>
 * <b>Decision Table:</b> <i>A table where each row represents a test case. A column can contain either a test input or an
 * expectation.</i>
 * <p>
 * LivingDoc will evaluate the fixture class for consistency when it is first loaded. Only decision table related annotations
 * can be used within a decision table fixture!
 *
 * The parameter parallel allows the specification via the annotation, that the rows should be executed in parallel. The default value
 * of parallel is false, that means if not specified the rows will be executed serial.
 *
 * @see Before
 * @see After
 * @see BeforeRow
 * @see AfterRow
 * @see BeforeFirstCheck
 * @see Input
 * @see Check
 * @since 2.0
 */
@Repeatable(DecisionTableFixture.DecisionTableFixtures.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DecisionTableFixture {

    String[] value() default "";
    boolean parallel() default false;

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @interface DecisionTableFixtures {
        DecisionTableFixture[] value();
    }

}
