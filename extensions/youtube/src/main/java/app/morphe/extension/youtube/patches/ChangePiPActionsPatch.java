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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import app.morphe.extension.shared.ResourceType;
import app.morphe.extension.shared.Utils;
import app.morphe.extension.shared.settings.EnumSetting;
import app.morphe.extension.youtube.settings.Settings;
import app.morphe.extension.youtube.shared.PlaybackController;

public class ChangePiPActionsPatch {
    // list of lists of strings, each list has the following items:
    // 0->resource id, 1->remoteaction title, 2->remoteaction content description, 3->action
    // null means use default remoteaction
    private static final Map<String, List<String>> actions = Map.of(
        "rewind", Arrays.asList(
            "quantum_ic_replay_10_white_24",
            "Rewind",
            "Rewind ten seconds",
                    "rewind"
        ),
        "fastforward", Arrays.asList(
            "quantum_ic_forward_10_white_24",
            "Fast forward",
            "Fast forward 10 seconds",
                    "fastforward"
        ),
        "prev", Arrays.asList(
            "quantum_ic_skip_previous_white_24",
            "Previous",
            "Previous video",
                    "prev"
        ),
        "next", Arrays.asList(
            "quantum_ic_skip_next_white_24",
            "Next",
            "Next video",
                    "next"
        )
    );

    private static RemoteAction listToRA(List<String> action) {
        return new RemoteAction(
                Icon.createWithResource(
                        Utils.getContext(),
                        getIdentifierOrThrow(ResourceType.DRAWABLE, action.get(0))
                ),
                action.get(1),
                action.get(2),
                PendingIntent.getBroadcast(
                        Utils.getContext(),
                        0,
                        new Intent(action.get(3)).setPackage(Utils.getContext().getPackageName()),
                        PendingIntent.FLAG_IMMUTABLE
                )
        );
    }

    static Receiver receiver = new Receiver();

    public static List<RemoteAction> getActionList(List<RemoteAction> actionList) {
        // very basic handling to not overwrite the pip actions if it is not using the default actions; may change this to be more robust if it causes issues
        if (actionList.size() != 3) {
            return actionList;
        }

        Context context = Utils.getContext();

        actionList = new ArrayList<>(actionList);

        RemoteAction backgroundAction = actionList.get(0);
        RemoteAction playpauseAction = actionList.get(1);

        List<EnumSetting<PiPActions>> settings = List.of(Settings.LEFT_PIP_ACTION, Settings.CENTER_PIP_ACTION, Settings.RIGHT_PIP_ACTION);
        for (int i = 0; i < 3; i++) {
            PiPActions setting = settings.get(i).get();
            if (setting == PiPActions.DEFAULT) {
                continue;
            }
            actionList.set(
                    i, switch (setting) {
                        case REWIND -> listToRA(Objects.requireNonNull(actions.get("rewind")));
                        case FF -> listToRA(Objects.requireNonNull(actions.get("fastforward")));
                        case PREV -> listToRA(Objects.requireNonNull(actions.get("prev")));
                        case NEXT -> listToRA(Objects.requireNonNull(actions.get("next")));
                        case BG -> backgroundAction;
                        case PLAY -> playpauseAction;
                        default -> null;
                    }
            );

            if (setting.actionName != null) {
                // newer sdks require a flag for if the receiver is exported or not, so we check for that here
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    context.registerReceiver(receiver, new IntentFilter(setting.actionName), Context.RECEIVER_NOT_EXPORTED);
                } else {
                    context.registerReceiver(receiver, new IntentFilter(setting.actionName));
                }
            }
        }
        return actionList;
    }

    public enum PiPActions {
        DEFAULT(null),
        REWIND("rewind"),
        FF("fastforward"),
        PREV("prev"),
        NEXT("next"),
        BG(null),
        PLAY(null);

        final String actionName;

        PiPActions(String action) {
            this.actionName = action;
        }
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
            case "rewind":
                PlaybackController.skip(-10);
                break;
            case "fastforward":
                PlaybackController.skip(10);
                break;
            case "next":
                PlaybackController.next();
                break;
            case "prev":
                PlaybackController.prev();
                break;
        }
    }
}
