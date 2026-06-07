package app.morphe.patches.youtube.misc.pictureinpicture

import app.morphe.patcher.patch.bytecodePatch

val ChangePiPActionsPatch = bytecodePatch(
    name = "Change PiP Actions",
    description = "Changes the actions shown in the picture in picture interface."
) {
    execute {
        PiPFingerprint.let {
            val filter = it.instructionMatches[0]
        }
    }
}