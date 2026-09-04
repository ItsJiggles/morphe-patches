package app.morphe.extension.youtube.patches;

import static app.morphe.extension.shared.ResourceUtils.getIdentifierOrThrow;

import android.app.PendingIntent;
import android.app.RemoteAction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import app.morphe.extension.shared.ResourceType;
import app.morphe.extension.shared.Utils;
import app.morphe.extension.youtube.shared.PlaybackController;

public class ChangePiPActionsPatch {
    private static final Map<String, List<String>> actions = Map.of(
        "rewind", Arrays.asList(
            "quantum_ic_replay_10_white_24",
            "Rewind",
            "Rewind ten seconds",
            "com.google.android.youtube.action.pip.rewind"
        ),
        "fastforward", Arrays.asList(
            "quantum_ic_forward_10_white_24",
            "Fast forward",
            "Fast forward 10 seconds",
            "com.google.android.youtube.action.pip.fastforward"
        ),
        "prev", Arrays.asList(
            "quantum_ic_skip_previous_white_24",
            "Previous",
            "Previous video",
            "com.google.android.youtube.action.pip.prev"
        ),
        "next", Arrays.asList(
            "quantum_ic_skip_next_white_24",
            "Next",
            "Next video",
            "com.google.android.youtube.action.pip.next"
        )
    );
    // list of lists of strings, each list has the following items:
    // 0->resource id, 1->remoteaction title, 2->remoteaction content description, 3->action
    // null means use default remoteaction
    // i might change the actions to not use the com.google.android.youtube prefix later, we'll see
    private static final List<List<String>> userActionList = Arrays.asList(
        actions.get("rewind"),
        null,
        actions.get("fastforward")
    );
    static Receiver receiver = new Receiver();

    public static void log(String message) {
        Log.d("pippatch", message);
    }

    public static List<RemoteAction> getActionList(List<RemoteAction> actionList) {
        // very basic handling to not overwrite the pip actions if it is not using the default actions; may change this to be more robust if it causes issues
        if (actionList.size() != 3) {
            return actionList;
        }

        Context context = Utils.getContext();

        actionList = new ArrayList<>(actionList);
        for (int i = 0; i < 3; i++) {
            List<String> action = userActionList.get(i);
            // if null, just use the default action
            if (action == null) {
                continue;
            }
            actionList.set(i,
                new RemoteAction(
                    Icon.createWithResource(
                        context,
                        getIdentifierOrThrow(ResourceType.DRAWABLE, action.get(0))
                    ),
                    action.get(1),
                    action.get(2),
                    PendingIntent.getBroadcast(
                        context,
                        0,
                        new Intent(action.get(3)).setPackage(context.getPackageName()),
                        PendingIntent.FLAG_IMMUTABLE
                    )
                )
            );

            // newer sdks require a flag for if the receiver is exported or not, so we check for that here
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.registerReceiver(receiver, new IntentFilter(action.get(3)), Context.RECEIVER_NOT_EXPORTED);
            } else {
                context.registerReceiver(receiver, new IntentFilter(action.get(3)));
            }
        }
        return actionList;
    }
}

class Receiver extends BroadcastReceiver {
    @Override
    public final void onReceive(Context context, Intent intent) {
        // i dont think this will ever be null but better safe than sorry
        if (intent.getAction() == null) {
            return;
        }
        switch (intent.getAction()) {
            case "com.google.android.youtube.action.pip.rewind":
                PlaybackController.skip(-10);
                break;
            case "com.google.android.youtube.action.pip.fastforward":
                PlaybackController.skip(10);
                break;
            case "com.google.android.youtube.action.pip.next":
                PlaybackController.next();
                break;
            case "com.google.android.youtube.action.pip.prev":
                PlaybackController.prev();
                break;
        }
    }
}
