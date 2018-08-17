package org.livingdoc.junit.engine.descriptors

import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.support.descriptor.EngineDescriptor
import org.junit.platform.engine.support.hierarchical.Node
import org.livingdoc.junit.engine.LivingDocContext

class EngineDescriptor(uniqueId: UniqueId) : EngineDescriptor(uniqueId, "LivingDoc"), Node<LivingDocContext>
