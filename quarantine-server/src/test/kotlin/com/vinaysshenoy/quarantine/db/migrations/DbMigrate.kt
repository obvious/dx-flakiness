package com.vinaysshenoy.quarantine.db.migrations

import com.vinaysshenoy.quarantine.extensions.MigrationHelperParameterResolver
import org.junit.jupiter.api.extension.ExtendWith

@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@ExtendWith(MigrationHelperParameterResolver::class)
annotation class DbMigrate(val changesToApply: Int)