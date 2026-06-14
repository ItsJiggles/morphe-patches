package app.morphe.extension.youtube.shared;

import android.media.session.MediaController;
import android.media.session.MediaSession;

import java.util.Objects;

public class PlaybackController {
    private static MediaSession capturedSession;
    private static MediaController controller;
    private static MediaController.TransportControls transportControls;

    public static void captureSession(MediaSession session) {
        capturedSession = session;
        controller = capturedSession.getController();
        transportControls = controller.getTransportControls();
    }

    public static void skip(int seconds) {
        if (capturedSession == null) {
            return;
        }
        long position = Objects.requireNonNull(controller.getPlaybackState()).getPosition();
        transportControls.seekTo(position + seconds * 1000L);
    }
}
