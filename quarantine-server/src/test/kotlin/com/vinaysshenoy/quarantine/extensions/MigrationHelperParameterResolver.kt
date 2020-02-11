package com.vinaysshenoy.quarantine.extensions

import com.vinaysshenoy.quarantine.db.migrations.DbMigrate
import com.vinaysshenoy.quarantine.db.migrations.MigrationHelper
import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ParameterContext
import org.junit.jupiter.api.extension.support.TypeBasedParameterResolver
import org.junit.platform.commons.support.AnnotationSupport.findAnnotation
import java.lang.reflect.AnnotatedElement
import java.lang.reflect.Method

class MigrationHelperParameterResolver : AfterEachCallback, TypeBasedParameterResolver<MigrationHelper>() {

    companion object {
        private val EXTENSION_NAMESPACE = ExtensionContext.Namespace.create(Any())
        private val KEY = Any()
    }

    override fun afterEach(context: ExtensionContext) {
        context
            .getStore(EXTENSION_NAMESPACE)
            .remove(KEY, MigrationHelper::class.java)?.cleanUp()
    }

    override fun resolveParameter(
        parameterContext: ParameterContext,
        extensionContext: ExtensionContext
    ): MigrationHelper {
        val migrate = resolveMigrationAnnotation(extensionContext)

        val helper = MigrationHelper(migrate.changesToApply)

        extensionContext
            .getStore(EXTENSION_NAMESPACE)
            .put(KEY, helper)

        return helper
    }

    private fun resolveMigrationAnnotation(context: ExtensionContext): DbMigrate {
        val annotatedElement: AnnotatedElement = context.element.orElseThrow()
        var annotation: DbMigrate? = null

        if(annotatedElement is Method) {
            annotation = findAnnotation(annotatedElement, DbMigrate::class.java).orElseGet {
                val testClass = context.testClass.orElseThrow()

                findAnnotation(testClass, DbMigrate::class.java).orElse(null)
            }
        }

        return requireNotNull(annotation) { "Could not annotation [${DbMigrate::class.java.name}] on either the test method or the class!" }
    }
}