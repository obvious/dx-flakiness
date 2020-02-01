package com.vinaysshenoy.quarantine.resources.payloads

import com.fasterxml.jackson.annotation.JsonProperty
import com.vinaysshenoy.quarantine.dao.FlakyStatus
import javax.validation.constraints.NotBlank

data class TestCasePayload(

    @NotBlank
    @JsonProperty("class")
    val testClass: String,

    @NotBlank
    @JsonProperty("name")
    val testName: String,

    @JsonProperty("flakyStatus")
    val flakyStatus: FlakyStatus
)