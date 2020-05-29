package org.livingdoc.engine.execution.groups

import org.livingdoc.api.documents.Group
import org.livingdoc.engine.execution.documents.DocumentFixture

/**
 * ImplicitGroup represents an implicit group assigned to [DocumentFixtures][DocumentFixture] without any other group.
 *
 * ImplicitGroup is used as the [GroupFixture] for these [DocumentFixtures][DocumentFixture]
 *
 * @see GroupFixture
 * @see DocumentFixture
 */
@Group
internal class ImplicitGroup
