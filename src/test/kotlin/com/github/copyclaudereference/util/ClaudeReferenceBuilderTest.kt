package com.github.copyclaudereference.util

import org.junit.Assert.assertEquals
import org.junit.Test

class ClaudeReferenceBuilderTest {

    @Test
    fun `file path only`() {
        assertEquals("@src/Main.kt", ClaudeReferenceBuilder.build("src/Main.kt"))
    }

    @Test
    fun `folder path`() {
        assertEquals("@src/components", ClaudeReferenceBuilder.build("src/components"))
    }

    @Test
    fun `single line`() {
        assertEquals("@src/Main.kt#L5", ClaudeReferenceBuilder.build("src/Main.kt", 5, 5))
    }

    @Test
    fun `multiple lines`() {
        assertEquals("@src/Main.kt#L5-10", ClaudeReferenceBuilder.build("src/Main.kt", 5, 10))
    }

    @Test
    fun `start line only`() {
        assertEquals("@src/Main.kt#L5", ClaudeReferenceBuilder.build("src/Main.kt", 5, null))
    }

    @Test
    fun `null start line`() {
        assertEquals("@src/Main.kt", ClaudeReferenceBuilder.build("src/Main.kt", null, null))
    }

    @Test
    fun `root file`() {
        assertEquals("@package.json", ClaudeReferenceBuilder.build("package.json"))
    }

    @Test
    fun `deeply nested path`() {
        assertEquals(
            "@src/components/ui/Button.tsx#L10-25",
            ClaudeReferenceBuilder.build("src/components/ui/Button.tsx", 10, 25)
        )
    }

    @Test
    fun `line 1`() {
        assertEquals("@src/Main.kt#L1", ClaudeReferenceBuilder.build("src/Main.kt", 1, 1))
    }
}
