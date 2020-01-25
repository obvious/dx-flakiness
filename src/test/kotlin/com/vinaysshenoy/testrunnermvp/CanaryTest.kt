package com.vinaysshenoy.testrunnermvp

import org.junit.Test
import org.opentest4j.AssertionFailedError
import strikt.api.expectThat
import strikt.assertions.isEqualTo

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