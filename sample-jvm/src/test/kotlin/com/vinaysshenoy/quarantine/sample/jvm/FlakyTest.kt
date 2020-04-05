package com.vinaysshenoy.quarantine.sample.jvm

import com.vinaysshenoy.quarantine.QuarantineTestRule
import org.junit.Rule
import org.junit.Test
import strikt.api.expectThat
import strikt.assertions.isGreaterThan
import java.util.*
import kotlin.random.Random

class FlakyTest {

    @get:Rule
    val rule = QuarantineTestRule()

    @Test
    fun `flaky test that fails 50% of the time`() {
        expectThat(Random.nextFloat()) isGreaterThan 0.5F
    }

    @Test
    fun `flaky test that fails 30% of the time`() {
        expectThat(Random.nextFloat()) isGreaterThan 0.3F
    }

    class NestedFlakyTest {

        @get:Rule
        val rule = QuarantineTestRule()

        @Test
        fun `flaky test that fails 80% of the time`() {
            expectThat(Random.nextFloat()) isGreaterThan 0.8F
        }

        @Test
        fun `flaky test that fails 70% of the time`() {
            expectThat(Random.nextFloat()) isGreaterThan 0.7F
        }
    }
}