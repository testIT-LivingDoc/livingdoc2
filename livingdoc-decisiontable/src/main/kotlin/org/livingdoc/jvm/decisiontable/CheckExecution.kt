package org.livingdoc.jvm.decisiontable

import org.livingdoc.repositories.model.decisiontable.Field
import org.livingdoc.repositories.model.decisiontable.Header
import org.livingdoc.results.Status
import org.livingdoc.results.examples.decisiontables.FieldResult
import kotlin.reflect.KFunction
import kotlin.reflect.jvm.javaMethod

class CheckExecution(
    private val checkMethod: KFunction<*>,
    private val header: Header,
    private val field: Field
) {

    fun execute(): FieldResult {
        val fieldResultBuilder = FieldResult.Builder()
            .withValue(field.value)
            .withCheckMethod(checkMethod.javaMethod!!)

        try {
            FixtureMethodInvoker.invoke(checkMethod, field.value)
            fieldResultBuilder.withStatus(Status.Executed)
        } catch (@Suppress("TooGenericExceptionCaught") e: Throwable) {
            fieldResultBuilder.withStatus(Status.Exception(e))
        }

        return fieldResultBuilder.build()
    }
}
