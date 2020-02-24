package org.livingdoc.jvm.api.extension

/**
 * Marker interface for all extensions.
 *
 * An Extension can be registered declaratively via @Extensions, or automatically via the ServiceLoader mechanism. For
 * details on the latter, consult the User Guide.
 *
 * # Constructor Requirements
 * Extension implementations must have a default constructor
 */
interface Extension
