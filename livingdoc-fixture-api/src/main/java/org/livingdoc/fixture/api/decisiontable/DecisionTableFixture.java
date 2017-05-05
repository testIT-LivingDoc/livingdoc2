package org.livingdoc.fixture.api.decisiontable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Repeatable(DecisionTableFixture.DecisionTableFixtures.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DecisionTableFixture {

    String[] value();

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @interface DecisionTableFixtures {
        DecisionTableFixture[] value();
    }

}
