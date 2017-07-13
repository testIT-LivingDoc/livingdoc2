package org.livingdoc.api.fixtures.scenarios;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Specifies a template for scenario steps. If a scenario step matches the template, the annotated method will be
 * called during execution (see {@link ScenarioFixture}).
 * <p>
 * Templates for scenario steps are simply strings and can contain optional parameters. Examples:
 * <ul>
 *     <li><code>"Peter puts a banana into the basket."</code></li>
 *     <li><code>"{user} puts a {product} into the basket."</code></li>
 * </ul>
 * Matches between scenario steps and step templates do not have to be exact. The templates above would
 * all match the following steps:
 * <ul>
 *     <li><code>Peter puts a banana into the basket.</code></li>
 *     <li><code>Peter puts an apple into his basket.</code></li>
 * </ul>
 * The step template is used to identify parametres in scenario steps. For example, the second of the above templates
 * would extract the following parameters:
 * <ul>
 *     <li><code>Peter puts a banana into the basket. (user = "Peter", product = "banana")</code></li>
 *     <li><code>Peter puts an apple into his basket. (user = "Peter", product = "apple")</code></li>
 * </ul>
 * Extracted parameters are passed to the annotated method. Mapping of extracted parameters to parameter lists can
 * be controlled with the {@link Binding} parameter annotation.
 *
 * @see ScenarioFixture
 * @since 2.0
 */
@Repeatable(Step.Steps.class)
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Step {

    String[] value();

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface Steps {
        Step[] value();
    }

}
