package org.livingdoc.api.documents

/**
 * This annotation is used to mark a class as an executable document. The Living Doc engine looks for this annotation to
 * find the documents that should be executed. The annotated class can contain [DecisionTableFixture]
 * and [ScenarioFixture]s. The executed contents are referenced as a parameter
 * of the annotation.
 *
 * An ExecutableDocument can be grouped by using the [Group] annotation.
 * An ExecutableDocument can be disabled by using the [Disabled] annotation.
 */
/*
@Testable
@Target(AnnotationTarget.CLASS)
annotation class ExecutableDocument(
    val value: String,
    /**
     * The [Group] that this ExecutableDocument belongs too.
     *
     * This augments the usual lookup functionality, in which an ExecutableDocument that is declared nested inside a
     * class annotated with [Group] is considered part of that group.
     *
     * Note that an ExecutableDocument can only belong to a single group. It is an error if the [Group] specified
     * by this attribute on the annotation and the [Group] discovered by the nested class lookup are different.
     *
     * @see Group
     */
    val group: KClass<*> = Any::class,
    val fixtureClasses: Array<KClass<*>> = []
)
+*/
