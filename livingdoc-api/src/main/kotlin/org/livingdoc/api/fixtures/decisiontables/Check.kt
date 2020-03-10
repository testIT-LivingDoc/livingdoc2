package org.livingdoc.api.fixtures.decisiontables

import org.livingdoc.api.conversion.Converter
import org.livingdoc.api.conversion.TypeConverter

/**
 * Methods annotated with this annotation will be treated as expectation checks of a [DecisionTableFixture].
 *
 * The expectation value will be taken from the column matching one of the [.value] mappings. All checks will be
 * executed after all [Input] annotated methods were invoked.
 *
 * Annotated methods have to have exactly one parameter. If this parameter is of one of the natively supported types, it
 * will be set using one of LivingDoc's [TypeConverter] implementations. If you want to use your own, or the type is
 * not natively supported, you wil have to configure a [TypeConverter] either directly on the method or on the fixture
 * class using the [Converter] annotation.
 *
 * The annotation can have multiple [values][.value]. One usecase for this might be having multiple languages for your
 * documentation or having changed the column name between versions of your documentation you still want to maintain.
 *
 * @see DecisionTableFixture
 * @see Input
 * @see Converter
 * @see TypeConverter
 *
 * @since 2.0
 */
@Repeatable
@Target(AnnotationTarget.FUNCTION)
annotation class Check(val value: String)
