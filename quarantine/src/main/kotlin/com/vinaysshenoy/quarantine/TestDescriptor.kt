package com.vinaysshenoy.quarantine

import com.vinaysshenoy.quarantine.TestDescriptor.FlakinessStatus.*
import org.junit.runner.Description

data class TestDescriptor(
    val testClass: String,
    val testMethod: String,
    val flakinessStatus: FlakinessStatus
) {
    companion object {

        fun fromDescription(description: Description): List<TestDescriptor> {
            val descriptors = mutableListOf<TestDescriptor>()

            description
                .children
                .map(::fromDescription)
                .forEach { descriptors += it }

            if (description.isTest) {
                val descriptor = TestDescriptor(
                    testClass = description.className,
                    testMethod = description.methodName,
                    flakinessStatus = Unknown
                )
                descriptors.add(descriptor)
            }

            return descriptors
        }
    }

    val isFlaky: Boolean
        get() = flakinessStatus == Flaky

    fun asNotFlaky(): TestDescriptor {
        return copy(flakinessStatus = NotFlaky)
    }

    fun asFlaky(): TestDescriptor {
        return copy(flakinessStatus = Flaky)
    }

    enum class FlakinessStatus {
        Unknown,
        Flaky,
        NotFlaky
    }
}