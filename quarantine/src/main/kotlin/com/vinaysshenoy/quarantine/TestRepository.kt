package com.vinaysshenoy.quarantine

interface TestRepository {

    fun config(): Config

    fun add(descriptors: List<TestDescriptor>)

    fun record(clazz: String, method: String, isFlaky: Boolean)

    fun results(): List<TestDescriptor>

    fun pushResultsToCloud()
}