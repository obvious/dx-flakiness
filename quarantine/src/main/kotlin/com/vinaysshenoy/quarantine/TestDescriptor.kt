package com.vinaysshenoy.quarantine

import com.fasterxml.jackson.annotation.JsonProperty
import com.vinaysshenoy.quarantine.TestDescriptor.FlakinessStatus.*
import org.junit.runner.Description

data class TestDescriptor(
    @JsonProperty(value = "class")
    val testClass: String,

    @JsonProperty(value = "name")
    val testMethod: String,

    @JsonProperty(value = "flakyStatus")
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