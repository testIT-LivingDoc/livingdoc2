package org.livingdoc.engine.algo

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource


class DamerauLevenshteinTest {

    private val cut = DamerauLevenshtein()

    @ParameterizedTest
    @ValueSource(strings = ["", "a", "LivingDoc"])
    fun identicalStringsHaveDistanceOfZero(a: String) {
        assertThat(cut.distance(a, a)).isEqualTo(0)
    }

    @Test
    fun insertionsAndDeletionsAreCountedCorrectly() {
        assertThat(cut.distance("", "LivingDoc")).isEqualTo(9)
        assertThat(cut.distance("LivingDoc", "")).isEqualTo(9)
        assertThat(cut.distance("Living", "LivingDoc")).isEqualTo(3)
        assertThat(cut.distance("LivingDoc", "Living")).isEqualTo(3)
    }

    @Test
    fun substitutionsAreCountedCorrectly() {
        assertThat(cut.distance("Test-It", "Tast-It")).isEqualTo(1)
        assertThat(cut.distance("Test-It", "Tast-Et")).isEqualTo(2)
    }

    @Test
    fun swappingAdjacentCharactersCountsOnlyOnce() {
        assertThat(cut.distance("LivingDoc", "LviingDoc")).isEqualTo(1)
        assertThat(cut.distance("LivingDoc", "LviingoDc")).isEqualTo(2)
    }

    @Test
    fun returnsCutoffDistanceWhenDistanceIsTooLarge() {
        assertThat(
            cut.distance(
                "he adds '{}' to his shopping cart",
                "the user '{}' is logged into the shop"
            )
        ).isEqualTo(25)
        val withCutoffDistance = DamerauLevenshtein(cutoffDistance = 12)
        assertThat(
            withCutoffDistance.distance(
                "he adds '{}' to his shopping cart",
                "the user '{}' is logged into the shop"
            )
        ).isEqualTo(12)
    }

    @Test
    fun canConfigureWeightsOfInsertionsAndDeletions() {
        assertThat(cut.distance("Living", "ingDoc")).isEqualTo(6)
        val withExpensiveInsertionsAndDeletions = DamerauLevenshtein(weightDeletion = 2, weightInsertion = 2)
        assertThat(withExpensiveInsertionsAndDeletions.distance("Living", "ingDoc")).isEqualTo(6)
    }

    @Test
    fun canConfigureWeightOfSubstitutions() {
        assertThat(cut.distance("Living", "Loving")).isEqualTo(1)
        val withExpensiveSubstitutions = DamerauLevenshtein(
            weightDeletion = 3,
            weightInsertion = 3,
            weightSubstitution = 2
        )
        assertThat(withExpensiveSubstitutions.distance("Living", "Loving")).isEqualTo(2)
    }

    @Test
    fun canConfigureWeightOfTranspositions() {
        assertThat(cut.distance("Living", "Livign")).isEqualTo(1)
        val withExpensiveTranspositions = DamerauLevenshtein(
            weightDeletion = 3,
            weightInsertion = 3,
            weightSubstitution = 3,
            weightTransposition = 2
        )
        assertThat(withExpensiveTranspositions.distance("Living", "Livign")).isEqualTo(2)
    }

}

