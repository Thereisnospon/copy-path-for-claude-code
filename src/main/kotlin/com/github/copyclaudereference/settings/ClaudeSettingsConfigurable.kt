package com.github.copyclaudereference.settings

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.options.ShowSettingsUtil
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.dsl.builder.*
import javax.swing.JComponent
import javax.swing.JSpinner
import javax.swing.SpinnerNumberModel

class ClaudeSettingsConfigurable : Configurable {

    private var showNotificationCheckbox: JBCheckBox? = null
    private var durationSpinner: JSpinner? = null
    private var durationRow: Row? = null

    override fun getDisplayName(): String = "Copy Path for Claude Code"

    override fun createComponent(): JComponent {
        val settings = ClaudeSettings.getInstance()

        showNotificationCheckbox = JBCheckBox("Show notification after copy", settings.state.showNotification)
        durationSpinner = JSpinner(SpinnerNumberModel(settings.state.notificationDurationSeconds, 1, 30, 1))

        val panel = panel {
            group("General") {
                row {
                    cell(showNotificationCheckbox!!).onChanged {
                        durationRow?.enabled(it.isSelected)
                    }
                }
                durationRow = row("Notification duration (seconds):") {
                    cell(durationSpinner!!)
                }.enabled(settings.state.showNotification)
            }
            group("Keyboard Shortcut") {
                row {
                    text("Default shortcut: <b>Alt+C</b> (⌥C on macOS)")
                }
                row {
                    button("Configure in Keymap...") {
                        val keymapPanel = com.intellij.openapi.keymap.impl.ui.KeymapPanel()
                        ShowSettingsUtil.getInstance().editConfigurable(
                            null as java.awt.Component?,
                            keymapPanel
                        ) {
                            keymapPanel.selectAction("CopyPathForClaudeCode")
                        }
                    }
                }
            }
        }

        return panel
    }

    override fun isModified(): Boolean {
        val settings = ClaudeSettings.getInstance()
        return showNotificationCheckbox?.isSelected != settings.state.showNotification
                || (durationSpinner?.value as? Int) != settings.state.notificationDurationSeconds
    }

    override fun apply() {
        val settings = ClaudeSettings.getInstance()
        settings.state.showNotification = showNotificationCheckbox?.isSelected ?: true
        settings.state.notificationDurationSeconds = (durationSpinner?.value as? Int) ?: 3
    }

    override fun reset() {
        val settings = ClaudeSettings.getInstance()
        showNotificationCheckbox?.isSelected = settings.state.showNotification
        durationSpinner?.value = settings.state.notificationDurationSeconds
        durationRow?.enabled(settings.state.showNotification)
    }

    override fun disposeUIResources() {
        showNotificationCheckbox = null
        durationSpinner = null
        durationRow = null
    }
}
