package app.morphe.patches.youtube.misc.pictureinpicture

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.methodCall

internal object SetPiPActionsFingerprint : Fingerprint(
    filters = listOf(
        methodCall(
            definingClass = "Landroid/app/PictureInPictureParams", name = "setActions"
        )
    )
)

internal object MediaSessionFingerprint : Fingerprint(
    filters = listOf(
        methodCall(
            definingClass = "Landroid/media/session/MediaSession",
            name = "setActive"
        )
    )
)