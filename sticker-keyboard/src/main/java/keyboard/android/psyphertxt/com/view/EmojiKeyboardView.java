package keyboard.android.psyphertxt.com.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import keyboard.android.psyphertxt.com.EmojiKeyboardService;
import keyboard.android.psyphertxt.com.R;
import keyboard.android.psyphertxt.com.adapter.EmojiPagerAdapter;
import keyboard.android.psyphertxt.com.stickers.StickerActivity;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.NativeExpressAdView;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class EmojiKeyboardView extends View implements SharedPreferences.OnSharedPreferenceChangeListener,SmartTabLayout.TabProvider{

    private static final String TAG = EmojiKeyboardView.class.getSimpleName();

    private ViewPager viewPager;
    private SmartTabLayout pagerSlidingTabStrip;
    private LinearLayout layout;
    private LinearLayout admobFrame;
    private EmojiPagerAdapter emojiPagerAdapter;
    private EmojiKeyboardService emojiKeyboardService;
    SharedPreferences prefs;
    Boolean withExpressions;

    public EmojiKeyboardView(Context context) {
        super(context);
        initialize(context);
    }

    public EmojiKeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public EmojiKeyboardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
    }

    private void initialize(Context context) {

        emojiKeyboardService = (EmojiKeyboardService) context;

        prefs = context.getSharedPreferences(
                "sticker_app", Context.MODE_PRIVATE);

        withExpressions = prefs.getBoolean("icons_with_text", false);

        LayoutInflater inflater = (LayoutInflater)   getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        layout = (LinearLayout) inflater.inflate(R.layout.keyboard_main, null);

        admobFrame = (LinearLayout) layout.findViewById(R.id.admob_frame);

        viewPager = (ViewPager) layout.findViewById(R.id.emojiKeyboard);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                final AdRequest adRequest = new AdRequest.Builder()
                        .addTestDevice("CCDF3FFB9F1C5F61511338E52C46D7E3")  // My Galaxy Nexus test phone
                        .build();
                final NativeExpressAdView mAdView = (NativeExpressAdView) layout.findViewById(R.id.adView);
                //mAdView.setAdUnitId("ca-app-pub-1112176598912130/5415713809");
                //mAdView.setAdSize(AdSize.MEDIUM_RECTANGLE);
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
            }
        }, 4000);

        pagerSlidingTabStrip = (SmartTabLayout) layout.findViewById(R.id.emojiCategorytab);

        pagerSlidingTabStrip.setCustomTabView(this);
        //pagerSlidingTabStrip.setSelectedTabIndicatorColor(getResources().getColor(R.color.holo_blue));

        //pagerSlidingTabStrip.setSelectedTabIndicatorHeight(6);

        emojiPagerAdapter = new EmojiPagerAdapter(context, viewPager, height, withExpressions);

        viewPager.setAdapter(emojiPagerAdapter);

        setupDeleteButton();

        setupGoToNextActivityButton();

        setupSwitchButton();

        pagerSlidingTabStrip.setViewPager(viewPager);

        viewPager.setCurrentItem(1);

        PreferenceManager.getDefaultSharedPreferences(context).registerOnSharedPreferenceChangeListener(this);
    }

    public View getView() {
        return layout;
    }

    public void notifyDataSetChanged() {
        emojiPagerAdapter.notifyDataSetChanged();
        viewPager.refreshDrawableState();
    }

    private void setupDeleteButton() {

        ImageView delete = (ImageView) layout.findViewById(R.id.deleteButton);

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imeManager = (InputMethodManager) v.getContext().getApplicationContext().getSystemService(INPUT_METHOD_SERVICE);
                imeManager.showInputMethodPicker();
            }
        });

        delete.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                emojiKeyboardService.switchToPreviousInputMethod();
                return false;
            }
        });
    }

    private void setupSwitchButton() {

        final ImageView switchBtn = (ImageView) layout.findViewById(R.id.switchButton);

        switchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                withExpressions = withExpressions == false ? true : false;
                prefs.edit().putBoolean("icons_with_text", withExpressions).apply();
                switchBtn.setImageResource(withExpressions == false ? R.drawable.shape_trans : R.drawable.shape);
                emojiPagerAdapter.notifyDataChanged(switchBtn.getContext(), withExpressions);
            }
        });
    }

    private void setupGoToNextActivityButton() {

        ImageView gotoNextActivity = (ImageView) layout.findViewById(R.id.openActivityButton);

        gotoNextActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), StickerActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                v.getContext().startActivity(intent);
            }
        });

    }


    private int width;
    private int height;
    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        width = View.MeasureSpec.getSize(widthMeasureSpec);
        height = View.MeasureSpec.getSize(heightMeasureSpec);

        Log.d("emojiKeyboardView", width +" : " + height);
        setMeasuredDimension(width, height);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        Log.d("sharedPreferenceChange", "function called on change of shared preferences with key " + key);
        if (key.equals("icon_set")){
            emojiPagerAdapter = new EmojiPagerAdapter(getContext(), viewPager, height, withExpressions);
            viewPager.setAdapter(emojiPagerAdapter);
            this.invalidate();
        }
    }

    @Override
    public View createTabView(ViewGroup container, int position, PagerAdapter adapter) {
        Resources res = container.getContext().getResources();
        TextView icon = new TextView(container.getContext());
        LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity= Gravity.CENTER;
        layoutParams.setMargins(10,0,10,0);
        icon.setLayoutParams(layoutParams);
        icon.setPadding(20,0,20,0);
        icon.setTextColor(Color.parseColor("#535353"));
        icon.setTypeface(Typeface.SANS_SERIF);
        icon.setTextSize(14);
        icon.setText(getResources().getString(R.string.app_name));
        return icon;
    }

    private void showNativeAd() {
        admobFrame.setVisibility(View.GONE);
        Log.i(TAG, "iN FACEBOOK NATIVE");
        AdView adView = new AdView(admobFrame.getContext(), "1582301235134013_1582364931794310", AdSize.BANNER_HEIGHT_50);
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
}
