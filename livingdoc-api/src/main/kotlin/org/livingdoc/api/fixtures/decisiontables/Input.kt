package org.livingdoc.api.fixtures.decisiontables

import org.livingdoc.api.conversion.Converter
import org.livingdoc.api.conversion.TypeConverter

/**
 * Methods or fields annotated with this annotation will be treated as test inputs of a [DecisionTableFixture].
 *
 * The input value will be taken from the column matching one of the [value] mappings. All inputs will be set before
 * any [Check] annotated method is invoked.
 *
 * If a field is annotated and it is of one of the natively supported types, it will be set using one of LivingDoc's
 * [TypeConverter] implementations. If you want to use your own, or the type is not natively supported, you wil have to
 * configure a [TypeConverter] either directly on the field or on the fixture class using the [Converter] annotation.
 *
 * If a method is annotated it has to have exactly one parameter! As with fields, custom [TypeConverter] might have to
 * be configured.
 *
 * The annotation can have multiple [values][value]. One use case for this might be having multiple languages for your
 * documentation or having changed the column name between versions of your documentation you still want to maintain.
 *
 * @see DecisionTableFixture
 * @see Check
 * @see Converter
 * @see TypeConverter
 *
 * @since 2.0
 */
@Target(
    AnnotationTarget.PROPERTY,
    AnnotationTarget.FIELD,
    AnnotationTarget.FUNCTION
)
annotation class Input(val value: String)
