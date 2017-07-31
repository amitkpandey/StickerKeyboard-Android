package keyboard.android.psyphertxt.com.stickers;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import keyboard.android.psyphertxt.com.R;

public class StickerLauncherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_sticker_launcher);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(StickerLauncherActivity.this, StickerActivity.class));
                finish();
            }
        }, 2000);
    }
}
