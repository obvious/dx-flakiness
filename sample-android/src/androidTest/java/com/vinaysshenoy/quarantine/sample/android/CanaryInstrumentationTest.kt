package com.vinaysshenoy.quarantine.sample.android

import com.vinaysshenoy.quarantine.QuarantineTestRule
import org.junit.Rule
import org.junit.Test
import org.opentest4j.AssertionFailedError
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class CanaryInstrumentationTest {

    @get:Rule
    val rule = QuarantineTestRule()

    @Test
    fun test_framework_must_work_as_expected() {
        expectThat(2 + 2) isEqualTo 4
    }

    @Test(expected = AssertionFailedError::class)
    fun test_framework_works_as_expected_for_failing_test() {
        expectThat(2 + 2) isEqualTo 1
    }
}