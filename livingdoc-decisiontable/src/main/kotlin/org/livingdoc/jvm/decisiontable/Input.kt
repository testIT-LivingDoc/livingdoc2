package org.livingdoc.jvm.decisiontable

import org.livingdoc.repositories.model.decisiontable.Field
import org.livingdoc.repositories.model.decisiontable.Header
import org.livingdoc.results.Status
import org.livingdoc.results.examples.decisiontables.FieldResult

class Input(
    private val header: Header,
    private val field: Field
) {

    fun setInput(): FieldResult {
        val fieldResultBuilder = FieldResult.Builder()
            .withValue(field.value)

        fieldResultBuilder.withStatus(Status.Executed)

        return fieldResultBuilder.build()
    }
}
