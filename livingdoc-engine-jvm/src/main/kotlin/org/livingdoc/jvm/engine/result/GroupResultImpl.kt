package org.livingdoc.jvm.engine.result

import org.livingdoc.results.Status
import org.livingdoc.results.documents.DocumentResult
import org.livingdoc.results.groups.GroupResult

data class GroupResultImpl(override val documentResults: List<DocumentResult>, override val status: Status) :
    GroupResult
