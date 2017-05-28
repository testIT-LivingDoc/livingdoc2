package org.livingdoc.fixture.api.decisiontable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.livingdoc.fixture.api.converter.Converter;
import org.livingdoc.fixture.api.converter.TypeConverter;


/**
 * Methods or fields annotated with this annotation will be treated as test inputs of a {@link DecisionTableFixture}.
 * <p>
 * The input value will be taken from the column matching one of the {@link #value()} mappings. All inputs will be set
 * before any {@link Check} annotated method is invoked.
 * <p>
 * If a field is annotated and it is of one of the natively supported types, it will be set using one of LivingDoc's {@link
 * TypeConverter} implementations. If you want to use your own, or the type is not natively supported, you wil have to
 * configure a {@link TypeConverter} either directly on the field or on the fixture class using the {@link Converter}
 * annotation.
 * <p>
 * If a method is annotated it has to have exactly one parameter! As with fields, custom {@link TypeConverter} might have to
 * be configured.
 * <p>
 * The annotation can have multiple {@link #value() values}. One usecase for this might be having multiple languages for
 * your documentation or having changed the column name between versions of your documentation you still want to maintain.
 *
 * @see DecisionTableFixture
 * @see Check
 * @see Converter
 * @see TypeConverter
 * @since 2.0
 */
@Repeatable(Input.Inputs.class)
@Target({ ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Input {

    String[] value();

    @Target({ ElementType.FIELD, ElementType.METHOD })
    @Retention(RetentionPolicy.RUNTIME)
    @interface Inputs {
        Input[] value();
    }

}
