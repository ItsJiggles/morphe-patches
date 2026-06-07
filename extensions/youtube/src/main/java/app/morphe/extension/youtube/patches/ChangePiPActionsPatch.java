package app.morphe.extension.youtube.patches;

import android.app.RemoteAction;
import android.util.Log;

import java.util.List;

public class ChangePiPActionsPatch {
    public static void log(String message) {
        Log.d("pippatch", String.valueOf(message));
    }

    public static void log(List<RemoteAction> message) {
//        for (RemoteAction i : message) {
//            Log.d("pippatch", String.valueOf(i.getTitle()));
//        }
        Log.d("pippatch", message.toString());
    }

    public static List<RemoteAction> getActionList() {
        return List.of();
    }
}
