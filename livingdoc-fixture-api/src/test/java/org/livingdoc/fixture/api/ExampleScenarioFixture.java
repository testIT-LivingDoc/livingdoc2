package org.livingdoc.fixture.api;

import java.util.List;

import org.livingdoc.fixture.api.converter.Converter;
import org.livingdoc.fixture.api.converter.TypeConverter;
import org.livingdoc.fixture.api.scenario.After;
import org.livingdoc.fixture.api.scenario.Before;
import org.livingdoc.fixture.api.scenario.ScenarioFixture;
import org.livingdoc.fixture.api.scenario.Step;


@ScenarioFixture("Example")
@ScenarioFixture({ "Beispiel", "Ejemplo" })
@Converter(ExampleScenarioFixture.MovieConverter.class)
public class ExampleScenarioFixture {

    @Before
    public void before() {

    }

    @After
    public void after() {

    }

    @Step("the user '{}' is logged in")
    @Step("der Benutzer '{}' ist angemeldet")
    public void loggedInUser(String username) {

    }

    @Step("he adds '{}' to the shopping cart")
    public void addToCart(Movie movie) {

    }

    @Step("he removes '{}' from the shopping cart")
    public void removeFromCart(Movie movie) {

    }

    @Step("the shopping cart contains {}")
    public void cartContains(List<Movie> movies) {

    }

    public static class Movie {

    }

    public static class MovieConverter implements TypeConverter<Movie> {

        @Override
        public Movie convert(String value) {
            return null;
        }

    }

}
