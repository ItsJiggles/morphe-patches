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

import java.util.Arrays;
import java.util.List;

import app.morphe.extension.shared.ResourceType;
import app.morphe.extension.shared.Utils;


public class ChangePiPActionsPatch {
    // list of lists of strings, each list has the following items:
    // 0->resource id, 1->remoteaction title, 2->remoteaction content description, 3->action
    // null means use default remoteaction
    // i might change the actions to not use the com.google.android.youtube prefix later, we'll see
    private static final List<List<String>> userActionList = Arrays.asList(
        Arrays.asList(
            "quantum_ic_replay_10_white_24",
            "Rewind",
            "Rewind ten seconds",
            "com.google.android.youtube.action.pip.rewind"
        ),
        null,
        Arrays.asList(
            "quantum_ic_forward_10_white_24",
            "Fast forward",
            "Fast forward 10 seconds",
            "com.google.android.youtube.action.pip.fastforward"
        )
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

        // theres almost certainly a better way to do this but im not good at java lol, ill fix it at some point probably
        actionList = Arrays.asList(actionList.get(0), actionList.get(1), actionList.get(2));
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
                // i will add functionality to these later, for now they just use placeholder log functions
                ChangePiPActionsPatch.log("rewind");
                break;
            case "com.google.android.youtube.action.pip.fastforward":
                ChangePiPActionsPatch.log("fast forward");
                break;
        }
    }
}
