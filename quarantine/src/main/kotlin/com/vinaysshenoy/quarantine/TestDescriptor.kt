package com.vinaysshenoy.quarantine

import org.junit.runner.Description

data class TestDescriptor(val testClass: String, val testMethod: String) {

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
                    testMethod = description.methodName
                )
                descriptors.add(descriptor)
            }

            return descriptors
        }
    }
}