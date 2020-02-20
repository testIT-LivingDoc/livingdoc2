package org.livingdoc.jvm.decisiontable;

import org.livingdoc.api.fixtures.decisiontables.Check;
import org.livingdoc.api.fixtures.decisiontables.Input;

public class TestJavaFixture {
    @Input("a")
    private Float headerA = 0.0F;
    private Float headerB = 0.0F;

    @Input("b")
    public void setB(Float valueB) {
        this.headerB = valueB;
    }

    @Check("a + b = ?")
    public void add() {
    }
}
