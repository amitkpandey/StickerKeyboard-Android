package keyboard.android.psyphertxt.com.stickers;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import keyboard.android.psyphertxt.com.R;

public class StickerFirstTimeActivity extends AppCompatActivity {

    private static final int PRIVACY_POLICY_REQUEST = 0;
    private static final int INSTALL_KEYBOARD_REQUEST = 1;
    private static final String TAG = StickerActivity.class.getSimpleName();

    Button installKeyboardBtn;
    TextView installLaterTextView, privacyPolicyTextView;
    static boolean firstOpen = false;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sticker_first_time);

        isStoragePermissionGranted(this);

        installKeyboardBtn = (Button) findViewById(R.id.btn_install_keyboard);
        installLaterTextView = (TextView) findViewById(R.id.tv_install_later);
        privacyPolicyTextView = (TextView) findViewById(R.id.tv_privacy_policy);

        prefs = this.getSharedPreferences(
                "sticker_app", Context.MODE_PRIVATE);
        prefs.edit().putBoolean("FIRST_LAUNCH", true).apply();
        installLaterTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), StickerActivity.class));
                finish();
            }
        });

        installKeyboardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firstOpen = true;
                prefs.edit().putBoolean("FIRST_CLICK", firstOpen).apply();
                Intent intent=new Intent(android.provider.Settings.ACTION_INPUT_METHOD_SETTINGS);
                startActivityForResult(intent, INSTALL_KEYBOARD_REQUEST);
            }
        });

        privacyPolicyTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firstOpen = true;
                prefs.edit().putBoolean("FIRST_CLICK", firstOpen).apply();
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("http://cyfa.io/privacy-policy/gstickers/sticker-privacy-policy.html"));
                startActivityForResult(i, PRIVACY_POLICY_REQUEST);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(prefs.getBoolean("FIRST_CLICK", false)) {
            startActivity(new Intent(StickerFirstTimeActivity.this, StickerActivity.class));
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case PRIVACY_POLICY_REQUEST:
                startActivity(new Intent(StickerFirstTimeActivity.this, StickerActivity.class));
                finish();
                break;
            case INSTALL_KEYBOARD_REQUEST:
                startActivity(new Intent(StickerFirstTimeActivity.this, StickerActivity.class));
                finish();
                break;
        }
    }

    public  static boolean isStoragePermissionGranted(Context context) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (context.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted");
                return true;
            } else {
                Log.v(TAG,"Permission is revoked");
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
            Log.v(TAG,"Permission: "+permissions[0]+ "was "+grantResults[0]);
            //resume tasks needing this permission
        }
    }

}
