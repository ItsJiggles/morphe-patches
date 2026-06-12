package app.morphe.patches.youtube.misc.pictureinpicture

import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.patch.bytecodePatch

private const val EXTENSION_CLASS = "Lapp/morphe/extension/youtube/patches/ChangePiPActionsPatch;"

val ChangePiPActionsPatch = bytecodePatch(
    name = "Change PiP Actions",
    description = "Changes the actions shown in the picture in picture interface."
) {
    execute {
        // get the list of actions to set in the pip interface and set them
        SetPiPActionsFingerprint.method.addInstructions(
            0, """
                invoke-static { p1 }, $EXTENSION_CLASS->getActionList(Ljava/util/List;)Ljava/util/List;
                move-result-object p1
            """
        )
    }
}