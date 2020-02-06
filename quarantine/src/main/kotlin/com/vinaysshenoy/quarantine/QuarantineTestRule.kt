package com.vinaysshenoy.quarantine

import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class QuarantineTestRule : TestRule {

    private val repository: TestRepository by lazy { InMemoryTestRepository.instance() }

    init {
        ReportFlakyTestsOnComplete.setup(repository)
    }

    override fun apply(statement: Statement, description: Description): Statement {
        val shouldQuarantineTests = repository.config().enabled
        return if (shouldQuarantineTests) {
            QuarantinedStatement(
                repository = repository,
                flakyTestRetryCount = 10,
                base = statement,
                description = description
            )
        } else {
            statement
        }
    }
}