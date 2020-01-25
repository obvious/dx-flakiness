package com.vinaysshenoy.quarantine

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
                    testClass = description.testClass.canonicalName,
                    testMethod = description.methodName,
                    flakinessStatus = FlakinessStatus.Unknown
                )
                descriptors.add(descriptor)
            }

            return descriptors
        }
    }

    fun asNotFlaky(): TestDescriptor {
        return copy(flakinessStatus = FlakinessStatus.NotFlaky)
    }

    fun asFlaky(): TestDescriptor {
        return copy(flakinessStatus = FlakinessStatus.Flaky)
    }

    enum class FlakinessStatus {
        Unknown,
        Flaky,
        NotFlaky
    }
}