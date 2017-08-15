package keyboard.android.psyphertxt.com.stickers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.NativeExpressAdView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import keyboard.android.psyphertxt.com.R;

public class StickerActivity extends AppCompatActivity {

    private static final String TAG = StickerAdapter.class.getSimpleName();
    @Bind(R.id.sticker_grid_list)
    RecyclerView stickerGridView;
    ViewGroup admobFrame;
    private NativeAd nativeAd;
    private LinearLayout adView;


    private List<Sticker> stickers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = this.getSharedPreferences(
                "sticker_app", Context.MODE_PRIVATE);
        if(!prefs.getBoolean("FIRST_LAUNCH", false)){
            startActivity(new Intent(StickerActivity.this, StickerFirstTimeActivity.class));
            finish();
        }
        setContentView(R.layout.activity_sticker);

        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_info);
        //upArrow.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        MobileAds.initialize(getApplicationContext(), "pub-1112176598912130");

        ButterKnife.bind(this);

        admobFrame = (ViewGroup) findViewById(R.id.admob_frame);

        setupAdapter();
        addAdMobView();
    }

    private void setupAdapter() {

        //initStickers static themes
        stickers = Sticker.initStickers(this);

        if (stickerGridView.getAdapter() == null) {
            if(stickers != null) {
                StickerAdapter adapter = new StickerAdapter(this, stickers);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                stickerGridView.setLayoutManager(mLayoutManager);
                stickerGridView.setItemAnimator(new DefaultItemAnimator());
                stickerGridView.setAdapter(adapter);
            }else{
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
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add:
                Intent intent=new Intent(android.provider.Settings.ACTION_INPUT_METHOD_SETTINGS);
                startActivity(intent);
                return true;
            case android.R.id.home:
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
//        nativeAd = new NativeAd(admobFrame.getContext(), "1582301235134013_1582364931794310");
//        nativeAd.setAdListener(new com.facebook.ads.AdListener() {
//            @Override
//            public void onError(Ad ad, AdError adError) {
//                Log.i(TAG, "ERROR FACEBOOK NATIVE "+ adError.getErrorMessage());
//            }
//
//            @Override
//            public void onAdLoaded(Ad ad) {
//
//                // Ad loaded callback
//                if (nativeAd != null) {
//                    nativeAd.unregisterView();
//                }
//
//                Log.i(TAG, "LOADING FACEBOOK NATIVE");
//                // Add the Ad view into the ad container.
//                ViewGroup nativeAdContainer = admobFrame;
//                LayoutInflater inflater = LayoutInflater.from(admobFrame.getContext());
//                // Inflate the Ad view.  The layout referenced should be the one you created in the last step.
//                adView = (LinearLayout) inflater.inflate(R.layout.audience_sticker_item, nativeAdContainer, false);
//                nativeAdContainer.addView(adView);
//
//                // Create native UI using the ad metadata.
//                ImageView nativeAdIcon = (ImageView) adView.findViewById(R.id.native_ad_icon);
//                TextView nativeAdTitle = (TextView) adView.findViewById(R.id.native_ad_title);
//                MediaView nativeAdMedia = (MediaView) adView.findViewById(R.id.native_ad_media);
//                TextView nativeAdSocialContext = (TextView) adView.findViewById(R.id.native_ad_social_context);
//                TextView nativeAdBody = (TextView) adView.findViewById(R.id.native_ad_body);
//                Button nativeAdCallToAction = (Button) adView.findViewById(R.id.native_ad_call_to_action);
//
//                // Set the Text.
//                nativeAdTitle.setText(nativeAd.getAdTitle());
//                nativeAdSocialContext.setText(nativeAd.getAdSocialContext());
//                nativeAdBody.setText(nativeAd.getAdBody());
//                nativeAdCallToAction.setText(nativeAd.getAdCallToAction());
//
//                // Download and display the ad icon.
//                NativeAd.Image adIcon = nativeAd.getAdIcon();
//                NativeAd.downloadAndDisplayImage(adIcon, nativeAdIcon);
//
//                // Download and display the cover image.
//                nativeAdMedia.setNativeAd(nativeAd);
//
//
//                // Register the Title and CTA button to listen for clicks.
//                List<View> clickableViews = new ArrayList<>();
//                clickableViews.add(nativeAdTitle);
//                clickableViews.add(nativeAdCallToAction);
//                if(nativeAd != null)
//                    nativeAd.registerViewForInteraction(nativeAdContainer,clickableViews);
//                admobFrame.setVisibility(View.VISIBLE);
//            }
//
//            @Override
//            public void onAdClicked(Ad ad) {
//                // Log.i(TAG, "LOADING FACEBOOK NATIVE");
//            }
//
//            @Override
//            public void onLoggingImpression(Ad ad) {
//
//            }
//        });

        // Request an ad
//        nativeAd.loadAd();
    }
}
