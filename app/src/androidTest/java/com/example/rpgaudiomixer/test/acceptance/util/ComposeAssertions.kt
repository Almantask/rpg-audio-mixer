package com.example.rpgaudiomixer.test.acceptance.util

import android.util.Log
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot

private const val MAX_DEBUG_TEXTS = 60
private const val MAX_DEBUG_DEPTH = 20
private const val MAX_DEBUG_NODES = 300
private const val LOG_TAG = "ComposeAssert"
private const val LOG_CHUNK_SIZE = 3800

fun SemanticsNodeInteractionsProvider.assertTextDisplayed(
    expectedText: String,
    ignoreCase: Boolean = false,
) {
    try {
        onNodeWithText(expectedText, ignoreCase = ignoreCase).assertIsDisplayed()
    } catch (e: AssertionError) {
        val root = onRoot(useUnmergedTree = true).fetchSemanticsNode()
        val visibleTexts = root.collectTexts().take(MAX_DEBUG_TEXTS)
        val semanticsDump = root.toDebugString(maxDepth = MAX_DEBUG_DEPTH, maxNodes = MAX_DEBUG_NODES)

        val message = buildString {
            appendLine("Compose text assertion failed")
            appendLine("Expected displayed text: \"$expectedText\" (ignoreCase=$ignoreCase)")
            appendLine()
            if (visibleTexts.isNotEmpty()) {
                appendLine("Displayed text candidates (first ${visibleTexts.size}):")
                visibleTexts.forEach { appendLine("- $it") }
                appendLine()
            }
            appendLine("Semantics tree (useUnmergedTree=true):")
            appendLine(semanticsDump)
        }

        logLong(LOG_TAG, message)

        throw AssertionError(message, e)
    }
}

private fun logLong(tag: String, message: String) {
    if (message.length <= LOG_CHUNK_SIZE) {
        Log.e(tag, message)
        return
    }

    var start = 0
    var part = 1
    while (start < message.length) {
        val end = (start + LOG_CHUNK_SIZE).coerceAtMost(message.length)
        Log.e(tag, "(part $part)\n" + message.substring(start, end))
        start = end
        part++
    }
}

private fun androidx.compose.ui.semantics.SemanticsNode.collectTexts(): List<String> {
    val result = LinkedHashSet<String>()

    fun visit(node: androidx.compose.ui.semantics.SemanticsNode) {
        node.config.getOrNull(SemanticsProperties.Text)
            ?.map { it.text }
            ?.filter { it.isNotBlank() }
            ?.forEach(result::add)

        node.config.getOrNull(SemanticsProperties.EditableText)
            ?.text
            ?.takeIf { it.isNotBlank() }
            ?.let(result::add)

        node.config.getOrNull(SemanticsProperties.ContentDescription)
            ?.filter { it.isNotBlank() }
            ?.forEach(result::add)

        node.children.forEach(::visit)
    }

    visit(this)
    return result.toList()
}

private fun androidx.compose.ui.semantics.SemanticsNode.toDebugString(
    maxDepth: Int,
    maxNodes: Int,
): String {
    val sb = StringBuilder()
    var visited = 0

    fun nodeLabel(node: androidx.compose.ui.semantics.SemanticsNode): String {
        val tag = node.config.getOrNull(SemanticsProperties.TestTag)
        val text = node.config.getOrNull(SemanticsProperties.Text)?.joinToString(" | ") { it.text }
        val editable = node.config.getOrNull(SemanticsProperties.EditableText)?.text
        val cd = node.config.getOrNull(SemanticsProperties.ContentDescription)?.joinToString(" | ")

        return buildString {
            if (!tag.isNullOrBlank()) append("tag=$tag ")
            if (!text.isNullOrBlank()) append("text=\"$text\" ")
            if (!editable.isNullOrBlank()) append("editable=\"$editable\" ")
            if (!cd.isNullOrBlank()) append("cd=\"$cd\" ")
        }.trim().ifBlank { "(no text/tag/cd)" }
    }

    fun visit(node: androidx.compose.ui.semantics.SemanticsNode, depth: Int) {
        if (visited >= maxNodes) return
        visited++

        sb.append("  ".repeat(depth.coerceAtLeast(0)))
        sb.appendLine(nodeLabel(node))

        if (depth >= maxDepth) return
        node.children.forEach { child ->
            if (visited < maxNodes) visit(child, depth + 1)
        }
    }

    visit(this, depth = 0)
    if (visited >= maxNodes) sb.appendLine("... (truncated at $maxNodes nodes)")
    return sb.toString()
}
