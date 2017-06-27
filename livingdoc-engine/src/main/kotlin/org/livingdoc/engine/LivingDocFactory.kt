package org.livingdoc.engine

import org.livingdoc.engine.dummy.DummyLivingDocImpl

class LivingDocFactory {
    fun createInstance(): LivingDoc = DummyLivingDocImpl()
}
