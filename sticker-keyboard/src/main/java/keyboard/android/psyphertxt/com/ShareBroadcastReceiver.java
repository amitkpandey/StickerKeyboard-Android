package keyboard.android.psyphertxt.com;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;

/**
 * Created by Lisa on 8/23/17.
 **/

public class ShareBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            Log.d("INFORMATION", "Received intent after selection: " + intent.getExtras().toString());
            String appName = "";
            for (String key : intent.getExtras().keySet()) {
                appName = String.valueOf(intent.getExtras().get(key));
                Log.d(getClass().getSimpleName(), " " + appName);
            }
            Answers.getInstance().logCustom(new CustomEvent("Share Click Event")
                    .putCustomAttribute("Name", appName));
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
