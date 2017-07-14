package org.livingdoc.engine.dummy

import org.livingdoc.engine.LivingDoc
import org.livingdoc.engine.execution.DocumentResult
import org.livingdoc.engine.execution.ExecutionException

class DummyLivingDocImpl : LivingDoc {

    @Throws(ExecutionException::class)
    override fun execute(document: Class<*>): DocumentResult {
        println("this would be the execution of: $document")
        return DocumentResult()
    }

}
