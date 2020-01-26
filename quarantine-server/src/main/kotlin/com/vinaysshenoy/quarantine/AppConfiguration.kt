package com.vinaysshenoy.quarantine

import com.fasterxml.jackson.annotation.JsonProperty
import io.dropwizard.Configuration
import javax.validation.constraints.NotEmpty

class AppConfiguration : Configuration() {

    @NotEmpty
    @JsonProperty
    lateinit var appName: String
}