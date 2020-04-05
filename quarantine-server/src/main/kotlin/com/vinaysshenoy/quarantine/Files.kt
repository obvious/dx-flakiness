package com.vinaysshenoy.quarantine

import java.nio.file.Paths

fun path(first: String, vararg parts: String): String {
    return Paths.get(first, *parts).toString()
}