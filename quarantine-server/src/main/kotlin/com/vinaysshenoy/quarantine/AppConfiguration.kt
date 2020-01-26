package com.vinaysshenoy.quarantine

import com.fasterxml.jackson.annotation.JsonProperty
import io.dropwizard.Configuration
import io.dropwizard.db.DataSourceFactory
import javax.validation.Valid
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

class AppConfiguration : Configuration() {

    @NotEmpty
    @JsonProperty
    lateinit var appName: String

    @Valid
    @NotNull
    lateinit var database: DataSourceFactory
}