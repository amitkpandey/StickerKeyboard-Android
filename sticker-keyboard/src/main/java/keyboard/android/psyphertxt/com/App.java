package keyboard.android.psyphertxt.com;

import android.app.Application;
import android.support.multidex.MultiDexApplication;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;

import io.fabric.sdk.android.Fabric;

/**
 * Created by Lisa on 8/11/17.
 **/

public class App extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(App.this, new Answers(), new Crashlytics());
    }
}
