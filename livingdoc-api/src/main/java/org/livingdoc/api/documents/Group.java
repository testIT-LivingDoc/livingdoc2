package org.livingdoc.api.documents;

import org.junit.platform.commons.annotation.Testable;
import org.livingdoc.api.After;
import org.livingdoc.api.Before;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A Group contains a number of {@link ExecutableDocument ExecutableDocuments}, which are executed together.
 *
 * The execution of different Groups is guaranteed to happen in sequence.
 *
 * A Group can also define {@link Before} and {@link After} hooks, which are run before or after all documents.
 *
 * @see ExecutableDocument
 * @see Before
 * @see After
 */
@Testable
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Group {
}
