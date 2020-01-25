package com.vinaysshenoy.quarantine

class InMemoryTestRepository : TestRepository {

    private var tests: List<TestDescriptor> = emptyList()

    private var results: List<TestDescriptor> = emptyList()

    override fun add(descriptors: List<TestDescriptor>) {
        tests = tests + descriptors
    }

    override fun record(clazz: String, method: String, isFlaky: Boolean) {
        val test = findTest(clazz, method)

        if (test != null) {
            val result = if (isFlaky) test.asFlaky() else test.asNotFlaky()
            results = results + result
        }
    }

    private fun findTest(clazz: String, method: String): TestDescriptor? {
        return tests.find { it.testClass == clazz && it.testMethod == method }
    }
}