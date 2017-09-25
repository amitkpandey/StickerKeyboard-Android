package keyboard.android.psyphertxt.com.stickers;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.NativeExpressAdView;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import keyboard.android.psyphertxt.com.BuildConfig;
import keyboard.android.psyphertxt.com.R;
import keyboard.android.psyphertxt.com.Utility;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;

import static keyboard.android.psyphertxt.com.stickers.StickerGridAdapter.bitmapPath;

public class StickerActivity extends AppCompatActivity {

    private static final String TAG = StickerActivity.class.getSimpleName();
    @Bind(R.id.sticker_grid_list)
    RecyclerView stickerGridView;
    ViewGroup admobFrame;
    boolean withExpressions = true;
    private List<Sticker> stickers;
    View switchMenu;
    MenuItem switchMenuItem;
    ViewTreeObserver viewTreeObserver;
    MaterialShowcaseView showcaseView;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = this.getSharedPreferences(
                "sticker_app", Context.MODE_PRIVATE);
        if(!prefs.getBoolean("FIRST_LAUNCH", false)){
            startActivity(new Intent(StickerActivity.this, StickerFirstTimeActivity.class));
            finish();
        }
        setContentView(R.layout.activity_sticker);

        deleteStickerEmojisOnStorage(StickerActivity.this);

        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_info);
        //upArrow.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        MobileAds.initialize(getApplicationContext(), "pub-1112176598912130");

        ButterKnife.bind(this);

        admobFrame = (ViewGroup) findViewById(R.id.admob_frame);

        withExpressions = prefs.getBoolean("icons_with_text", false);

        setupAdapter();
        addAdMobView();


        //if(BuildConfig.APPLICATION_ID.contains("naija")){
            implementWalkThrough();
       // }
    }

    private void setupAdapter() {

        //initStickers static themes
        stickers = Sticker.initStickers(this, withExpressions);

        if (stickerGridView.getAdapter() == null) {
            if(stickers != null) {
                StickerAdapter adapter = new StickerAdapter(this, stickers);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                stickerGridView.setLayoutManager(mLayoutManager);
                stickerGridView.setItemAnimator(new DefaultItemAnimator());
                stickerGridView.setAdapter(adapter);
            }else{
                Answers.getInstance().logCustom(new CustomEvent("Home Click Event")
                        .putCustomAttribute("Name", "Insufficient Memory"));
                new MaterialDialog.Builder(this)
                        .title("Insufficient Memory")
                        .content("You have insufficient memory to run this app. Please clear some memory and try again.")
                        .positiveText("ok")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        } else {
            ((StickerAdapter) stickerGridView.getAdapter()).refill(stickers);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        switchMenuItem = menu.findItem(R.id.switchicons);
        switchMenuItem.setIcon(withExpressions == false ? R.drawable.shape_trans : R.drawable.shape);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add:
                Answers.getInstance().logCustom(new CustomEvent("Home Click Event")
                        .putCustomAttribute("Name", "Add Keyboard"));
                Intent intent=new Intent(android.provider.Settings.ACTION_INPUT_METHOD_SETTINGS);
                startActivity(intent);
                return true;
            case R.id.switchicons:
                Answers.getInstance().logCustom(new CustomEvent("Home Click Event")
                        .putCustomAttribute("Name", "Switch Button"));
                withExpressions = withExpressions == false ? true : false;
                prefs.edit().putBoolean("icons_with_text", withExpressions).apply();
                switchMenuItem.setIcon(withExpressions == false ? R.drawable.shape_trans : R.drawable.shape);
                stickers = Sticker.initStickers(StickerActivity.this, withExpressions);
                StickerAdapter adapter = new StickerAdapter(this, stickers);
                stickerGridView.setAdapter(adapter);
                adapter.notifyDataSetChanged();

                return true;
            case android.R.id.home:
                Answers.getInstance().logCustom(new CustomEvent("Home Click Event")
                        .putCustomAttribute("Name", "StickerAbout Activity"));
                startActivity(new Intent(StickerActivity.this, StickerAboutActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void addAdMobView(){
        ViewGroup adCardView = admobFrame;
        AdRequest adRequest = new AdRequest.Builder()
//                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
                .addTestDevice("CCDF3FFB9F1C5F61511338E52C46D7E3")  // My Galaxy Nexus test phone
                .build();
        View view = View.inflate(admobFrame.getContext(), R.layout.admob_sticker_item, adCardView);
        final NativeExpressAdView mAdView = (NativeExpressAdView) view.findViewById(R.id.adView);
        mAdView.loadAd(adRequest);
        mAdView.setVisibility(View.GONE);
        mAdView.setAdListener(new AdListener(){
            @Override
            public void onAdLoaded() {
                mAdView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                showNativeAd();
            }
        });

        // The NativeExpressAdViewHolder recycled by the RecyclerView may be a different
        // instance than the one used previously for this position. Clear the
        // NativeExpressAdViewHolder of any subviews in case it has a different
        // AdView associated with it, and make sure the AdView for this position doesn't
        // already have a parent of a different recycled NativeExpressAdViewHolder.
        if (adCardView.getChildCount() > 0) {
            adCardView.removeAllViews();
        }
        if (mAdView.getParent() != null) {
            ((ViewGroup) mAdView.getParent()).removeView(mAdView);
        }

        // Add the Native Express ad to the native express ad view.
        adCardView.addView(mAdView);
    }

    private void showNativeAd() {
        admobFrame.setVisibility(View.GONE);
        Log.i(TAG, "iN FACEBOOK NATIVE");
        AdView adView = new AdView(this, "1582301235134013_1582364931794310", AdSize.BANNER_HEIGHT_50);
        adView.setAdListener(new com.facebook.ads.AdListener() {
            @Override
            public void onError(Ad ad, AdError adError) {
                Log.i(TAG, "ERROR FACEBOOK NATIVE "+ adError.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {
                admobFrame.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAdClicked(Ad ad) {

            }

            @Override
            public void onLoggingImpression(Ad ad) {

            }
        });
        admobFrame.addView(adView);
        adView.loadAd();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "in on Activity result");
        if(requestCode == 5 && resultCode == Activity.RESULT_OK){
            Log.d(TAG, "in if condition");
            try {
                Uri uri = Uri.parse(bitmapPath);
                File file = new File(uri.getPath());
                if(bitmapPath.startsWith("content://")){
                    ContentResolver contentResolver = getContentResolver();
                    contentResolver.delete(uri, null, null);
                }else {
                    if (file.exists()) {
                        Log.d(TAG, "in file exist");
                        if (file.delete()) {
                            Log.d(TAG, "in file delete");
                        }
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }


    private void deleteStickerEmojisOnStorage(Context context) {
        try {
            ArrayList<String> list = Utility.getArrayListString("filePaths", context);
            if (list != null) {
                if (!list.isEmpty()) {
                    for (String string : list) {
                        if (string != null) {
                            if (!string.isEmpty()) {
                                Uri uri = Uri.parse(string);
                                if (string.startsWith("content://")) {
                                    ContentResolver contentResolver = getContentResolver();
                                    contentResolver.delete(uri, null, null);
                                } else {
                                    File file = new File(uri.getPath());
                                    if (file.exists()) {
                                        Log.d(TAG, "in file exist");
                                        if (file.delete()) {
                                            Log.d(TAG, "in file delete");
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void implementWalkThrough(){
        System.out.println(">>>>"+BuildConfig.APPLICATION_ID);
        try {
            viewTreeObserver = getWindow().getDecorView().getViewTreeObserver();
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    switchMenu = findViewById(R.id.switchicons);
                    // This could be called when the button is not there yet, so we must test for null
                    if (switchMenu != null) {
                        // Found it! Do what you need with the button
                        int[] location = new int[2];
                        switchMenu.getLocationInWindow(location);
                        Log.d(TAG, "x=" + location[0] + " y=" + location[1]);

                        if (viewTreeObserver != null) {
                            // Now you can get rid of this listener
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                if (viewTreeObserver.isAlive()) {
                                    viewTreeObserver.removeOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                                        @Override
                                        public void onGlobalLayout() {

                                        }
                                    });
                                } else {
                                    viewTreeObserver = getWindow().getDecorView().getViewTreeObserver();
                                    viewTreeObserver.removeGlobalOnLayoutListener(this);
                                }
                            } else {
                                viewTreeObserver.removeGlobalOnLayoutListener(this);
                            }
                            try {
                                showcaseView = new MaterialShowcaseView.Builder(StickerActivity.this)
                                        .setTarget(switchMenu)
                                        .setDismissText("GOT IT")
                                        .setContentText("Tap on this menu to enable or disable stickers with text")
                                        .setDismissOnTargetTouch(true)
                                        .setContentTextColor(Color.parseColor("#dddddd"))
                                        .setDismissTextColor(Color.parseColor("#ffffff"))
                                        .setDelay(1000) // optional but starting animations immediately in onCreate can make them choppy
                                        .singleUse(BuildConfig.APPLICATION_ID) // provide a unique ID used to ensure it is only shown once
                                        .show();
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        } else {
                            viewTreeObserver = getWindow().getDecorView().getViewTreeObserver();
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                if (viewTreeObserver.isAlive()) {
                                    viewTreeObserver.removeOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                                        @Override
                                        public void onGlobalLayout() {

                                        }
                                    });
                                } else {
                                    viewTreeObserver = getWindow().getDecorView().getViewTreeObserver();
                                    viewTreeObserver.removeGlobalOnLayoutListener(this);
                                }
                            } else {
                                viewTreeObserver.removeGlobalOnLayoutListener(this);
                            }
                            try {
                                showcaseView = new MaterialShowcaseView.Builder(StickerActivity.this)
                                        .setTarget(switchMenu)
                                        .setDismissText("GOT IT")
                                        .setContentText("Tap on this menu to enable or disable stickers with text")
                                        .setDismissOnTargetTouch(true)
                                        .setContentTextColor(Color.parseColor("#dddddd"))
                                        .setDismissTextColor(Color.parseColor("#ffffff"))
                                        .setDelay(1000) // optional but starting animations immediately in onCreate can make them choppy
                                        .singleUse(BuildConfig.APPLICATION_ID) // provide a unique ID used to ensure it is only shown once
                                        .show();
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        if(showcaseView != null){
            try {
                showcaseView.hide();
            }catch (Exception e){
                super.onBackPressed();
            }
        }else{
            super.onBackPressed();
        }
    }
}
