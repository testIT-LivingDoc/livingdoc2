package org.livingdoc.engine.resources

import org.livingdoc.api.disabled.Disabled
import org.livingdoc.api.documents.ExecutableDocument

@Disabled("Skip this test document")
@ExecutableDocument("local://Calculator.html")
class DisabledExecutableDocument
