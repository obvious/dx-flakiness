package com.vinaysshenoy.quarantine.plugin.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.logging.Logger
import org.objectweb.asm.*
import org.objectweb.asm.ClassWriter.COMPUTE_FRAMES
import org.objectweb.asm.ClassWriter.COMPUTE_MAXS
import org.objectweb.asm.tree.AnnotationNode
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodNode
import java.io.File

@Suppress("UnstableApiUsage", "Unused")
class QuarantinePlugin : Plugin<Project> {

    private lateinit var logger: Logger

    override fun apply(project: Project) {
        logger = project.logger

        val quarantineTask = project.task("quarantineTransform") { task ->
            task.doLast {

                File(project.buildDir.absoluteFile, "classes")
                    .walkTopDown()
                    .filter(File::isJavaClassFile)
                    .map { it to ClassReader(it.readBytes()) }
                    .map { (file, classReader) ->
                        file to ClassNode().apply { classReader.accept(this, 0) }
                    }
                    .filter { (_, node) -> node.hasAtLeastOneJUnitTestMethod }
                    .onEach { (_, node) ->
                        val runWith = AnnotationNode("Lorg/junit/runner/RunWith;").apply {
                            values = listOf(
                                "value",
                                Type.getType("Lcom/vinaysshenoy/quarantine/QuarantineTestRunner;")
                            )
                        }
                        node.addAnnotation(runWith)
                    }
                    .toList()
                    .onEach { (file, node) ->
                        val classWriter = ClassWriter(COMPUTE_FRAMES or COMPUTE_MAXS)
                        node.accept(classWriter)

                        file.writeBytes(classWriter.toByteArray())
                    }
                    .toList()
            }
        }

        project
            .tasks
            .find { it.name == "testClasses" }
            ?.apply {
                finalizedBy(quarantineTask)
            }

    }
}

private val File.isJavaClassFile: Boolean
    get() = isFile && name.endsWith(".class")

private val ClassNode.hasAtLeastOneJUnitTestMethod: Boolean
    get() = methods != null && methods.any(MethodNode::isTestMethod)

private val MethodNode.isTestMethod: Boolean
    get() = visibleAnnotations != null && visibleAnnotations.any { it.desc == "Lorg/junit/Test;" }

private fun ClassNode.addAnnotation(annotationNode: AnnotationNode) {
    val annotations = mutableListOf(annotationNode)

    if (visibleAnnotations != null) {
        annotations.addAll(visibleAnnotations)
    }

    visibleAnnotations = annotations
}