package org.livingdoc.junit.engine

import org.junit.platform.engine.support.hierarchical.EngineExecutionContext
import org.livingdoc.jvm.engine.LivingDoc

class LivingDocContext : EngineExecutionContext {
    val livingDoc = LivingDoc.create()
}
