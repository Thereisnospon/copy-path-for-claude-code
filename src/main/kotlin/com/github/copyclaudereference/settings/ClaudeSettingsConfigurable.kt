package com.github.copyclaudereference.settings

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.options.ShowSettingsUtil
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.dsl.builder.*
import javax.swing.JComboBox
import javax.swing.JComponent
import javax.swing.JSpinner
import javax.swing.SpinnerNumberModel

class ClaudeSettingsConfigurable : Configurable {

    private var showNotificationCheckbox: JBCheckBox? = null
    private var durationSpinner: JSpinner? = null
    private var durationRow: Row? = null
    private var trailingSpaceCheckbox: JBCheckBox? = null
    private var multiFileSeparatorCombo: JComboBox<String>? = null

    override fun getDisplayName(): String = "Copy Path for Claude Code"

    override fun createComponent(): JComponent {
        val settings = ClaudeSettings.getInstance()

        showNotificationCheckbox = JBCheckBox("Show notification after copy", settings.state.showNotification)
        durationSpinner = JSpinner(SpinnerNumberModel(settings.state.notificationDurationSeconds, 1, 30, 1))

        trailingSpaceCheckbox = JBCheckBox("Append trailing space after copied reference", settings.state.appendTrailingSpace)

        val separatorOptions = arrayOf("Space", "Newline")
        multiFileSeparatorCombo = JComboBox(separatorOptions).apply {
            selectedIndex = settings.state.multiFileSeparator.ordinal
        }

        val panel = panel {
            group("Copy Format") {
                row {
                    cell(trailingSpaceCheckbox!!)
                }
                row("Multiple references separator:") {
                    cell(multiFileSeparatorCombo!!)
                        .comment("Separator between references when copying multiple files or multi-cursor selections")
                }
            }
            group("Notification") {
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
                || trailingSpaceCheckbox?.isSelected != settings.state.appendTrailingSpace
                || multiFileSeparatorCombo?.selectedIndex != settings.state.multiFileSeparator.ordinal
    }

    override fun apply() {
        val settings = ClaudeSettings.getInstance()
        settings.state.showNotification = showNotificationCheckbox?.isSelected ?: true
        settings.state.notificationDurationSeconds = (durationSpinner?.value as? Int) ?: 3
        settings.state.appendTrailingSpace = trailingSpaceCheckbox?.isSelected ?: false
        val separatorIndex = (multiFileSeparatorCombo?.selectedIndex ?: 0).coerceIn(0, ClaudeSettings.MultiFileSeparator.entries.lastIndex)
        settings.state.multiFileSeparator = ClaudeSettings.MultiFileSeparator.entries[separatorIndex]
    }

    override fun reset() {
        val settings = ClaudeSettings.getInstance()
        showNotificationCheckbox?.isSelected = settings.state.showNotification
        durationSpinner?.value = settings.state.notificationDurationSeconds
        durationRow?.enabled(settings.state.showNotification)
        trailingSpaceCheckbox?.isSelected = settings.state.appendTrailingSpace
        multiFileSeparatorCombo?.selectedIndex = settings.state.multiFileSeparator.ordinal
    }

    override fun disposeUIResources() {
        showNotificationCheckbox = null
        durationSpinner = null
        durationRow = null
        trailingSpaceCheckbox = null
        multiFileSeparatorCombo = null
    }
}
