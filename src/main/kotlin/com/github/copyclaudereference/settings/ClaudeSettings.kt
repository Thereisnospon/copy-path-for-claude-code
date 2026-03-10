package com.github.copyclaudereference.settings

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage

@State(
    name = "CopyPathForClaudeCodeSettings",
    storages = [Storage("CopyPathForClaudeCode.xml")]
)
class ClaudeSettings : PersistentStateComponent<ClaudeSettings.State> {

    data class State(
        var showNotification: Boolean = true,
        var notificationDurationSeconds: Int = 3
    )

    private var state = State()

    override fun getState(): State = state

    override fun loadState(state: State) {
        this.state = state
    }

    val showNotification: Boolean get() = state.showNotification
    val notificationDurationMs: Int get() = state.notificationDurationSeconds.coerceIn(1, 30) * 1000

    companion object {
        fun getInstance(): ClaudeSettings =
            ApplicationManager.getApplication().getService(ClaudeSettings::class.java)
    }
}
