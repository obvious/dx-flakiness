package com.vinaysshenoy.quarantine

import java.nio.file.Path

fun path(first: String, vararg parts: String): String {
    return Path.of(first, *parts).toString()
}