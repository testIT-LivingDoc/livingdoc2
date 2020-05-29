package org.livingdoc.api.documents;


import java.lang.annotation.*;

/**
 * This annotation is used to mark a document to use fail fast behaviour. Whenever a test fails, the whole execution
 * should stop.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface FailFast {
    /**
     * The Array of Exceptions that should lead to a fast fail
     */
    Class<? extends Throwable>[] onExceptionTypes() default Throwable.class;

}
