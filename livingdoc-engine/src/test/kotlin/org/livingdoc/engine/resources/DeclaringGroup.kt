package org.livingdoc.engine.resources

import org.livingdoc.api.disabled.Disabled
import org.livingdoc.api.documents.ExecutableDocument
import org.livingdoc.api.documents.Group

@Group
class DeclaringGroup {
    @Disabled
    @ExecutableDocument("", group = AnnotationGroup::class)
    class AmbiguousGroupExecutableDocument
}

@Group
class AnnotationGroup
