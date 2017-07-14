package org.livingdoc.api.conversion;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Annotating a fixture-class, -method or -field with this annotation will instruct LivingDoc to load all of the specified
 * {@link TypeConverter} for the corresponding context:
 * <ul>
 * <li>Annotating a <b>class</b> will use the {@link TypeConverter} for as long as the fixture instance exists.
 * Which is usually for the duration of a single test case. It will be used wherever possible (method parameters and fields)
 * unless another {@link TypeConverter} - who can handle the same type - is specified on the method or field.</li>
 * <li>Annotating a <b>method</b> will use the {@link TypeConverter} just for the conversion of that method's parameters.
 * It will be disregarding for all other methods, except if it is specified there as well.</li>
 * <li>Annotating a <b>field</b> will use the {@link TypeConverter} just for the initialization of that field.</li>
 * </ul>
 *
 * @since 2.0
 */
@Repeatable(Converter.Converters.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER })
public @interface Converter {

    Class<? extends TypeConverter>[] value();

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER })
    @interface Converters {
        Converter[] value();
    }

}
