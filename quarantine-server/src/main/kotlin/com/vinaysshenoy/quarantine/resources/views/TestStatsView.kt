package com.vinaysshenoy.quarantine.resources.views

import com.vinaysshenoy.quarantine.dao.TestStat
import io.dropwizard.views.View

data class TestStatsView(
    val stats: List<TestStat>
) : View("stats.mustache", Charsets.UTF_8)