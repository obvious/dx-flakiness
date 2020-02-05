package com.vinaysshenoy.quarantine.resources.views

import com.vinaysshenoy.quarantine.dao.TestStat
import io.dropwizard.views.View
import kotlin.math.roundToInt

data class TestStatsView(
    val stats: List<TestStatViewModel>
) : View("stats.mustache", Charsets.UTF_8) {

    companion object {
        fun fromStats(stats: List<TestStat>): TestStatsView = TestStatsView(stats.map(::TestStatViewModel))
    }

    data class TestStatViewModel(
        val clazz: String,
        val test: String,
        val flakiness: Int
    ) {
        constructor(testStat: TestStat) : this(
            testStat.className,
            testStat.testName,
            (testStat.flakinessRate * 100).roundToInt()
        )
    }
}