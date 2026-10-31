package app.morphe.patches.youtube.misc.pictureinpicture

import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.patch.bytecodePatch
import app.morphe.patches.shared.misc.settings.preference.ListPreference
import app.morphe.patches.shared.misc.settings.preference.PreferenceScreenPreference
import app.morphe.patches.youtube.misc.settings.PreferenceScreen
import app.morphe.patches.youtube.misc.settings.settingsPatch
import app.morphe.patches.youtube.shared.Constants.COMPATIBILITY_YOUTUBE

private const val EXTENSION_CLASS =
    "Lapp/morphe/extension/youtube/patches/ChangePiPActionsPatch;"
private const val PLAYBACK_CONTROLLER_CLASS =
    "Lapp/morphe/extension/youtube/shared/PlaybackController;"

val ChangePiPActionsPatch = bytecodePatch(
    name = "Change PiP Actions",
    description = "Changes the actions shown in the picture in picture interface."
) {
    dependsOn(
        settingsPatch,
    )

    compatibleWith(COMPATIBILITY_YOUTUBE)
    execute {
        PreferenceScreen.PLAYER.addPreferences(
            PreferenceScreenPreference(
                key = "morphe_change_pip_actions_screen",
                sorting = PreferenceScreenPreference.Sorting.UNSORTED,
                preferences = mutableSetOf(
                    ListPreference(
                        key = "morphe_left_pip_option",
                        entriesKey = "morphe_pip_actions_options",
                        entryValuesKey = "morphe_pip_actions_option_values"
                    ),
                    ListPreference(
                        key = "morphe_center_pip_option",
                        entriesKey = "morphe_pip_actions_options",
                        entryValuesKey = "morphe_pip_actions_option_values"
                    ),
                    ListPreference(
                        key = "morphe_right_pip_option",
                        entriesKey = "morphe_pip_actions_options",
                        entryValuesKey = "morphe_pip_actions_option_values"
                    )
                )
            )
        )
        // get the list of actions to set in the pip interface and set them
        SetPiPActionsFingerprint.method.addInstructions(
            0, """
                invoke-static { p1 }, $EXTENSION_CLASS->getActionList(Ljava/util/List;)Ljava/util/List;
                move-result-object p1
            """
        )
        // capture the MediaSession
        MediaSessionFingerprint.let {
            it.method.addInstructions(
                it.instructionMatches[0].index + 1, """
                    invoke-static { v0 }, $PLAYBACK_CONTROLLER_CLASS->captureSession(Landroid/media/session/MediaSession;)V
                """
            )
        }
    }
}