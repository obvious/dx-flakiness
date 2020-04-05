package com.vinaysshenoy.quarantine.sample.android

import com.vinaysshenoy.quarantine.QuarantineTestRule
import org.junit.Rule
import org.junit.Test
import strikt.api.expectThat
import strikt.assertions.isGreaterThan
import kotlin.random.Random

class FlakyTest {

    @get:Rule
    val rule = QuarantineTestRule()

    @Test
    fun flaky_test_that_fails_50p_of_the_time() {
        expectThat(Random.nextFloat()) isGreaterThan 0.5F
    }

    @Test
    fun flaky_test_that_fails_30p_of_the_time() {
        expectThat(Random.nextFloat()) isGreaterThan 0.3F
    }

    class NestedFlakyTest {

        @get:Rule
        val rule = QuarantineTestRule()

        @Test
        fun flaky_test_that_fails_80p_of_the_time() {
            expectThat(Random.nextFloat()) isGreaterThan 0.8F
        }

        @Test
        fun flaky_test_that_fails_70p_of_the_time() {
            expectThat(Random.nextFloat()) isGreaterThan 0.7F
        }
    }
}