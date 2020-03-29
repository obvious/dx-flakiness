package com.vinaysshenoy.testrunnermvp

import com.vinaysshenoy.quarantine.QuarantineTestRunner
import org.junit.Test
import org.junit.runner.RunWith
import org.opentest4j.AssertionFailedError
import strikt.api.expectThat
import strikt.assertions.isEqualTo

@RunWith(QuarantineTestRunner::class)
class CanaryTest {

    @Test
    fun `test framework must work as expected`() {
        expectThat(2 + 2) isEqualTo 4
    }

    @Test(expected = AssertionFailedError::class)
    fun `test framework works as expected for failing test`() {
        expectThat(2 + 2) isEqualTo 1
    }
}