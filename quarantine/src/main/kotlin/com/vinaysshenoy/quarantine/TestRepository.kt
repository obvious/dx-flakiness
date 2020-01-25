package com.vinaysshenoy.quarantine

interface TestRepository {

    fun add(descriptors: List<TestDescriptor>)

    fun record(clazz: String, method: String, isFlaky: Boolean)

    fun results(): List<TestDescriptor>
}