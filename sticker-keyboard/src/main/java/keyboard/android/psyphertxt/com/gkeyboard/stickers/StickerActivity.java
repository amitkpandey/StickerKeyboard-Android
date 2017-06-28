package keyboard.android.psyphertxt.com.gkeyboard.stickers;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.android.gms.ads.MobileAds;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import keyboard.android.psyphertxt.com.gkeyboard.R;

public class StickerActivity extends AppCompatActivity {

    private static final String TAG = StickerActivity.class.getSimpleName();

    @Bind(R.id.sticker_grid_list)
    RecyclerView stickerGridView;

    private List<Sticker> stickers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sticker);

        MobileAds.initialize(getApplicationContext(), "pub-1112176598912130");

        ButterKnife.bind(this);

        isStoragePermissionGranted(this);

        setupAdapter();
    }

    private void setupAdapter() {

        //initStickers static themes
        stickers = Sticker.initStickers(this);

        if (stickerGridView.getAdapter() == null) {
            StickerAdapter adapter = new StickerAdapter(this, stickers);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            stickerGridView.setLayoutManager(mLayoutManager);
            stickerGridView.setItemAnimator(new DefaultItemAnimator());
            stickerGridView.setAdapter(adapter);
        } else {
            ((StickerAdapter) stickerGridView.getAdapter()).refill(stickers);
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
