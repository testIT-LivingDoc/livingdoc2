package org.livingdoc.fixture.api.decisiontable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Repeatable(Input.Inputs.class)
@Target({ ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Input {

    String[] value();

    @Target({ ElementType.FIELD, ElementType.METHOD })
    @Retention(RetentionPolicy.RUNTIME)
    @interface Inputs {
        Input[] value();
    }

}
