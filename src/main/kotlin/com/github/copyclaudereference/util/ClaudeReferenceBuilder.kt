package com.github.copyclaudereference.util

object ClaudeReferenceBuilder {

    fun build(relativePath: String, startLine: Int? = null, endLine: Int? = null): String {
        val path = "@$relativePath"
        return when {
            startLine == null -> path
            endLine == null || endLine == startLine -> "$path#L$startLine"
            else -> "$path#L$startLine-$endLine"
        }
    }
}
