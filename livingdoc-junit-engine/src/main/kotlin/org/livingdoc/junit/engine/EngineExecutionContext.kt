package org.livingdoc.junit.engine

import org.junit.platform.engine.support.hierarchical.EngineExecutionContext
import org.livingdoc.engine.LivingDocFactory

class EngineExecutionContext : EngineExecutionContext {
    val livingDoc = LivingDocFactory().createInstance()
}
