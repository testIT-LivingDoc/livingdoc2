package org.livingdoc.engine.dummy

import org.livingdoc.engine.LivingDoc
import org.livingdoc.engine.execution.ExecutionException
import org.livingdoc.engine.execution.Result

class DummyLivingDocImpl : LivingDoc {

    @Throws(ExecutionException::class)
    override fun execute(document: Any): Result {
        println("this would be the execution of: $document")
        return object : Result {}
    }

}
