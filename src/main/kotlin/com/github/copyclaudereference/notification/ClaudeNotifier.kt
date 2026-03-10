package com.github.copyclaudereference.notification

import com.github.copyclaudereference.settings.ClaudeSettings
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.util.concurrency.AppExecutorUtil
import java.util.concurrent.TimeUnit

object ClaudeNotifier {

    fun notify(project: Project?, reference: String) {
        val settings = ClaudeSettings.getInstance()
        if (!settings.showNotification) return

        val notification = NotificationGroupManager.getInstance()
            .getNotificationGroup("Copy Path for Claude Code")
            .createNotification(
                "Copied Path for Claude Code",
                reference,
                NotificationType.INFORMATION
            )
        notification.notify(project)

        AppExecutorUtil.getAppScheduledExecutorService().schedule(
            { ApplicationManager.getApplication().invokeLater { notification.expire() } },
            settings.notificationDurationMs.toLong(),
            TimeUnit.MILLISECONDS
        )
    }
}
