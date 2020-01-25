package com.vinaysshenoy.testrunnermvp

import com.vinaysshenoy.quarantine.QuarantineTestRunner
import org.junit.Test
import org.junit.runner.RunWith
import strikt.api.expectThat
import strikt.assertions.isGreaterThan
import java.util.*

@RunWith(QuarantineTestRunner::class)
class FlakyTest {

    private val random = Random()

    @Test
    fun `flaky test that fails 50% of the time`() {
        expectThat(random.nextFloat()) isGreaterThan 0.5F
    }

    @Test
    fun `flaky test that fails 30% of the time`() {
        expectThat(random.nextFloat()) isGreaterThan 0.3F
    }

    @RunWith(QuarantineTestRunner::class)
    class NestedFlakyTest {

        private val random = Random()

        @Test
        fun `flaky test that fails 80% of the time`() {
            expectThat(random.nextFloat()) isGreaterThan 0.8F
        }

        @Test
        fun `flaky test that fails 70% of the time`() {
            expectThat(random.nextFloat()) isGreaterThan 0.7F
        }
    }
}