package app.morphe.patches.youtube.misc.pictureinpicture

import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.patch.bytecodePatch
import app.morphe.util.cloneMutable

private const val EXTENSION_CLASS = "Lapp/morphe/extension/youtube/patches/ChangePiPActionsPatch;"

val ChangePiPActionsPatch = bytecodePatch(
    name = "Change PiP Actions",
    description = "Changes the actions shown in the picture in picture interface."
) {
    execute {
        PiPFingerprint.let {
            it.method.apply {
                val helperMethod = cloneMutable("patch_PiP")
                it.classDef.methods.add(helperMethod)
                helperMethod.addInstructions(
                    0, """
                            invoke-static { }, $EXTENSION_CLASS->getActionList()Ljava/util/List;
                            move-result-object p1
                        """
                )
                addInstructions(
                    0, """
                    invoke-static { p0, p1 }, $helperMethod
                    move-result-object v1
                    return-object v1
                """
                )
            }
        }
    }
}