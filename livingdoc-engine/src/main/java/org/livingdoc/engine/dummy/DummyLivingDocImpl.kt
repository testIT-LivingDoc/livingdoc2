package org.livingdoc.engine.dummy

import org.livingdoc.engine.LivingDoc
import org.livingdoc.engine.execution.DocumentSelector
import org.livingdoc.engine.execution.ExecutionException
import org.livingdoc.engine.execution.Result

class DummyLivingDocImpl : LivingDoc {

    @Throws(ExecutionException::class)
    override fun execute(selector: DocumentSelector): Result {
        println("this would be a documentation execution")
        return object : Result {}
    }

}
