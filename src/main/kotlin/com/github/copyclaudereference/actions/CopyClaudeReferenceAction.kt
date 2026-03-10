package com.github.copyclaudereference.actions

import com.github.copyclaudereference.notification.ClaudeNotifier
import com.github.copyclaudereference.util.ClaudeReferenceBuilder
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.ide.CopyPasteManager
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.vfs.VfsUtilCore
import java.awt.datatransfer.StringSelection

class CopyClaudeReferenceAction : AnAction() {

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT

    override fun update(e: AnActionEvent) {
        val project = e.project
        val virtualFile = e.getData(CommonDataKeys.VIRTUAL_FILE)
        e.presentation.isEnabledAndVisible = project != null
                && virtualFile != null
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val projectDir = project.guessProjectDir() ?: return
        val editor = e.getData(CommonDataKeys.EDITOR)

        // Editor with selection → single file with line numbers
        if (editor != null && editor.selectionModel.hasSelection()) {
            val virtualFile = e.getData(CommonDataKeys.VIRTUAL_FILE) ?: return
            val relativePath = VfsUtilCore.getRelativePath(virtualFile, projectDir) ?: virtualFile.name
            val document = editor.document
            val selectionModel = editor.selectionModel

            val startLine = document.getLineNumber(selectionModel.selectionStart) + 1
            var endLine = document.getLineNumber(selectionModel.selectionEnd) + 1

            // Edge case: selection ends at the very beginning of the next line
            if (selectionModel.selectionEnd == document.getLineStartOffset(endLine - 1) && endLine > startLine) {
                endLine--
            }

            val reference = ClaudeReferenceBuilder.build(relativePath, startLine, endLine)
            CopyPasteManager.getInstance().setContents(StringSelection(reference))
            ClaudeNotifier.notify(project, reference)
            return
        }

        // Multi-file/folder selection from project tree or single file without selection
        val files = e.getData(CommonDataKeys.VIRTUAL_FILE_ARRAY)
        val selectedFiles = files?.takeIf { it.size > 1 }

        if (selectedFiles != null) {
            val references = selectedFiles.mapNotNull { file ->
                val path = VfsUtilCore.getRelativePath(file, projectDir) ?: file.name
                ClaudeReferenceBuilder.build(path)
            }
            val combined = references.joinToString(" ")
            CopyPasteManager.getInstance().setContents(StringSelection(combined))
            ClaudeNotifier.notify(project, "${references.size} paths copied")
        } else {
            val virtualFile = e.getData(CommonDataKeys.VIRTUAL_FILE) ?: return
            val relativePath = VfsUtilCore.getRelativePath(virtualFile, projectDir) ?: virtualFile.name
            val reference = ClaudeReferenceBuilder.build(relativePath)
            CopyPasteManager.getInstance().setContents(StringSelection(reference))
            ClaudeNotifier.notify(project, reference)
        }
    }
}
