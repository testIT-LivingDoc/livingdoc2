package org.livingdoc.engine.resources

import org.livingdoc.api.disabled.Disabled
import org.livingdoc.api.documents.ExecutableDocument
import org.livingdoc.api.fixtures.decisiontables.DecisionTableFixture

@ExecutableDocument("local://Calculator.html")
class DisabledDecisionTableDocument {

    @Disabled("Disabled DecisionTableFixture")
    @DecisionTableFixture(parallel = false)
    class DisabledDecisionTableFixture
}
