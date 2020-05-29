package org.livingdoc.engine.resources

import org.livingdoc.api.disabled.Disabled
import org.livingdoc.api.documents.ExecutableDocument
import org.livingdoc.api.fixtures.scenarios.ScenarioFixture

@ExecutableDocument("local://Calculator.html")
class DisabledScenarioDocument {

    @Disabled("Disabled ScenarioFixture")
    @ScenarioFixture
    class DisabledScenarioFixture
}
