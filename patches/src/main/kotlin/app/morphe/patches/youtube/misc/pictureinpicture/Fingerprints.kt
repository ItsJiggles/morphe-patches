package app.morphe.patches.youtube.misc.pictureinpicture

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.methodCall

// TODO: better name for the class
internal object PiPFingerprint : Fingerprint(
    filters = listOf(
        methodCall(
            definingClass = "Landroid/app/PictureInPictureParams", name = "setActions"
        )
    )
)