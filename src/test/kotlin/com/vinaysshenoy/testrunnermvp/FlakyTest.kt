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
}