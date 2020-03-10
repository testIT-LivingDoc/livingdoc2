package org.livingdoc.api.fixtures.scenarios

/**
 * Specifies a template for scenario steps. If a scenario step matches the template, the annotated method will be called
 * during execution (see [ScenarioFixture]).
 *
 * Templates for scenario steps are simply strings and can contain optional parameters. Examples:
 *  * `"Peter puts a banana into the basket."`
 *  * `"{user} puts a {product} into the basket."`
 *
 * Matches between scenario steps and step templates do not have to be exact. The templates above would all match the
 * following steps:
 *  * `Peter puts a banana into the basket.`
 *  * `Peter puts an apple into his basket.`
 *
 * The step template is used to identify parameters in scenario steps. For example, the second of the above templates
 * would extract the following parameters:
 *  * `Peter puts a banana into the basket. (user = "Peter", product = "banana")`
 *  * `Peter puts an apple into his basket. (user = "Peter", product = "apple")`
 *
 * Extracted parameters are passed to the annotated method. Mapping of extracted parameters to parameter lists can be
 * controlled with the [Binding] parameter annotation.
 *
 * @see ScenarioFixture
 *
 * @since 2.0
 */
@Target(AnnotationTarget.FUNCTION)
annotation class Step(vararg val value: String)
