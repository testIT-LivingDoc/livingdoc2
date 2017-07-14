package org.livingdoc.api.fixtures.decisiontables;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.livingdoc.api.conversion.Converter;
import org.livingdoc.api.conversion.TypeConverter;


/**
 * Methods annotated with this annotation will be treated as expectation checks of a {@link DecisionTableFixture}.
 * <p>
 * The expectation value will be taken from the column matching one of the {@link #value()} mappings. All checks will be
 * executed after all {@link Input} annotated methods were invoked.
 * <p>
 * Annotated methods have to have exactly one parameter. If this parameter is of one of the natively supported types, it
 * will be set using one of LivingDoc's {@link TypeConverter} implementations. If you want to use your own, or the type is
 * not natively supported, you wil have to configure a {@link TypeConverter} either directly on the method or on the fixture
 * class using the {@link Converter} annotation.
 * <p>
 * The annotation can have multiple {@link #value() values}. One usecase for this might be having multiple languages for
 * your documentation or having changed the column name between versions of your documentation you still want to maintain.
 *
 * @see DecisionTableFixture
 * @see Input
 * @see Converter
 * @see TypeConverter
 * @since 2.0
 */
@Repeatable(Check.Checks.class)
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Check {

    String[] value();

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface Checks {
        Check[] value();
    }

}
