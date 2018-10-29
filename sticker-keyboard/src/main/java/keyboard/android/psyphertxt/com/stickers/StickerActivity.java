package keyboard.android.psyphertxt.com.stickers;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextPaint;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import keyboard.android.psyphertxt.com.BuildConfig;
import keyboard.android.psyphertxt.com.R;
import keyboard.android.psyphertxt.com.Utility;
import keyboard.android.psyphertxt.com.products.IabHelper;
import keyboard.android.psyphertxt.com.products.IabResult;
import keyboard.android.psyphertxt.com.products.Inventory;
import keyboard.android.psyphertxt.com.products.Purchase;


public class StickerActivity extends AppCompatActivity {

    private static final String TAG = StickerActivity.class.getSimpleName();
    @BindView(R.id.sticker_grid_list)
    RecyclerView stickerGridView;
    boolean withExpressions = true;
    private List<Sticker> stickers;
    View switchMenu;
    MenuItem switchMenuItem;
    ViewTreeObserver viewTreeObserver;
    ShowcaseView showcaseView;
    SharedPreferences prefs, sharedPref;
    AdItem ad = null;
    StickerAdapter adapter;
    LinkedList<AdItem> ads;
    int randomInt = 0;
    IabHelper mHelper;
    List<StickerItem> items;
    List<String> skuList;
    int RC_REQUEST = 10001;
    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ads = new LinkedList<>();
        sharedPref = this.getSharedPreferences("Purchased", Context.MODE_PRIVATE);

        prefs = this.getSharedPreferences("sticker_app", Context.MODE_PRIVATE);
        if(!prefs.getBoolean("FIRST_LAUNCH", false)){
            startActivity(new Intent(StickerActivity.this, StickerFirstTimeActivity.class));
            finish();
        }
//        if(!prefs.getBoolean("USER_SIGNED_IN", false)){
//            FirebaseAuth.getInstance()
//                    .signInAnonymously()
//                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
//                        @Override
//                        public void onSuccess(AuthResult authResult) {
//                            System.out.println(">>>>>>>>"+authResult.toString());
//                            prefs.edit().putBoolean("USER_SIGNED_IN", true).commit();
//                            // Keep track of the referrer in the RTDB. Database calls
//                            // will depend on the structure of your app's RTDB.
//                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
////                            DatabaseReference userRecord =
////                                    FirebaseDatabase.getInstance().getReference()
////                                            .child("users")
////                                            .child(user.getUid());
////                            userRecord.child("referred_by").setValue(referrerUid);
//                        }
//
//                    })
//                    .addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//                    e.printStackTrace();
//                }
//            });
//        }
        setContentView(R.layout.activity_sticker);

        deleteStickerEmojisOnStorage(StickerActivity.this);

        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_info);
        //upArrow.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_ATOP);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        MobileAds.initialize(getApplicationContext(), "pub-1112176598912130");

        ButterKnife.bind(this);

        //admobFrame = findViewById(R.id.admob_frame);

        withExpressions = prefs.getBoolean("icons_with_text", false);
        ads.add(new AdItem("GhanaTok", "Kweku is a jovial character and a cool dude who is enthusiastic about creating humour out of situations. Ama is a sassy girl who brings a spark of attitude to any conversation. ", R.drawable.ghanatok_advert, "https://play.google.com/store/apps/details?id=keyboard.android.psyphertxt.com.gtokkeyboard", AdItem.ADTYPE.ANDROID, "INSTALL NOW"));
        ads.add(new AdItem("LoveTok", "Communicating your affections for someone can be tricky at first, but there are many other ways of expressing yourself other than blurting out, \"I love you\".", R.drawable.lovetok_advert, "https://play.google.com/store/apps/details?id=keyboard.android.psyphertxt.com.lovetok", AdItem.ADTYPE.ANDROID, "DOWNLOAD"));
        ads.add(new AdItem("Naija Dylog", "Funke and Chuku are cousins who are friendly in nature. Funke has a desire for good living and quality material assets. She is well liked by others for her sense of humour. Chuku is a pleasure loving person.", R.drawable.naija_dylog_advert, "https://play.google.com/store/apps/details?id=keyboard.android.psyphertxt.com.naijadylog&hl=en", AdItem.ADTYPE.ANDROID, "FREE"));
        ads.add(new AdItem("Food Stickers", "Love food? Express your cravings using these food stickers in your instant messaging chat applications (WhatsApp, Facebook messenger, Telegram, Viber, etc)", R.drawable.foodstickers_advert, "https://play.google.com/store/apps/details?id=keyboard.android.psyphertxt.com.foodstickers&hl=en", AdItem.ADTYPE.ANDROID, "INSTALL NOW"));
        ads.add(new AdItem("Bonanza", "is a simple card game that involves players using cards to form simple mathematical sentences that sum up to give either the highest or lowest whole number possible.", R.drawable.bonanza_advert, "http://www.daffodilsgp.com/portfolio-item/bonanza-card-game/", AdItem.ADTYPE.WEB, "FIND OUT MORE"));
        ads.add(new AdItem("Nudu", "is a simple board game with a simple objective… Fulfilment in life. Nudu recreates lives journey to fulfilment in an exciting board game that people of all ages will enjoy.", R.drawable.nudu_advert, "+233 50 162 0994", AdItem.ADTYPE.PHONE, "BUY NOW"));
        ads.add(new AdItem("G-Match", "Can you match all the stickers in the puzzle with the limited number of moves and time? Challenge yourself and match the stickers to earn rewards", R.drawable.gmatch_advert, "http://gstickerss.com/games", AdItem.ADTYPE.WEB, "PLAY NOW"));
        ads.add(new AdItem("G-Stickers", "provide new forms of sticker expressions through nonverbal languages worth millions. Combining our classic characters with hilarious situations and priceless phrases, these stickers will help you express your likes (or dislikes) without saying a word.", R.drawable.gstickers_advert, "http://gstickerss.com/games", AdItem.ADTYPE.WEB, "FIND OUT MORE"));
        if(BuildConfig.APPLICATION_ID.contains("lovetok")){
            ads.remove(1);
        } else if(BuildConfig.APPLICATION_ID.contains("gtokkeyboard")){
            ads.remove(0);
        } else if(BuildConfig.APPLICATION_ID.contains("foodstickers")){
            ads.remove(3);
        } else if(BuildConfig.APPLICATION_ID.contains("naijadylog")){
            ads.remove(2);
        }

        skuList = Arrays.asList(StickerActivity.this.getString(R.string.purchase_itemId));
        activity = StickerActivity.this;
        items = new ArrayList<>();

        //initStickers static themes
        //stickers = Sticker.initStickers(this, withExpressions);

        String base64EncodedPublicKey = getResources().getString(R.string.base64);
        mHelper = new IabHelper(this, base64EncodedPublicKey);

        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    Toast.makeText(activity, "Problem setting up In-app Billing: " + result, Toast.LENGTH_SHORT).show();
                } else {
                    loadProducts();
                }
            }
        });

        //setupAdapter();
        //addAdMobView();


        if(!BuildConfig.APPLICATION_ID.contains("gkeyboard") && !BuildConfig.APPLICATION_ID.contains("lovetok")){
            try {
                implementWalkThrough();
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        // Get deep link from result (may be null if no link is found)
                        Uri deepLink = null;
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();
                        }
                        if(deepLink != null){
                            Map<String, String> hm = Utility.splitQuery(deepLink.toString());
                            if(hm.containsKey("utm_campaign")){
                                Answers.getInstance().logCustom(new CustomEvent("App Install Campaign")
                                        .putCustomAttribute("Campaign", hm.get("utm_campaign")));
                            }else{
                                Answers.getInstance().logCustom(new CustomEvent("App Install")
                                        .putCustomAttribute("Organic", deepLink.toString()));
                            }
                            System.out.println("++++++++++++++++"+deepLink.toString());
                        }

                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "getDynamicLink:onFailure", e);
                    }
                });




    }

    private void setupAdapter() {
       /* skuList = Arrays.asList(StickerActivity.this.getString(R.string.purchase_itemId));
        activity = StickerActivity.this;
        items = new ArrayList<>();

        //initStickers static themes
        stickers = Sticker.initStickers(this, withExpressions);
        String base64EncodedPublicKey = getResources().getString(R.string.base64);
        mHelper = new IabHelper(this, base64EncodedPublicKey);

        //In-app Purchase
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    Toast.makeText(activity, "Problem setting up In-app Billing: " + result, Toast.LENGTH_SHORT).show();
                } else {
                    loadProducts();
                }
            }
        });*/

        //initStickers static themes
        stickers = Sticker.initStickers(this, withExpressions);

        if (stickerGridView.getAdapter() == null) {
            if(stickers != null) {
                randomInt = new Random().nextInt();
                ad = ads.get(randomInt < 0 ?   (randomInt * -1) % ads.size() : randomInt % ads.size());
                //adapter = new StickerAdapter(this, stickers, ad);
                adapter = new StickerAdapter(this,stickers, mHelper, mPurchaseFinishedListener, items, ad);
                GridLayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 3, LinearLayout.VERTICAL, false);
                mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        switch (adapter.getItemViewType(position)) {
                            case 0:
                                return 3;
                            case 1:
                                return 1;
                            default:
                                return 1;
                        }
                    }
                });
                stickerGridView.setLayoutManager(mLayoutManager);
                stickerGridView.setItemAnimator(new DefaultItemAnimator());
                stickerGridView.setAdapter(adapter);


                Timer progressTimer = new Timer();
                ProgressTimerTask   timeTask = new ProgressTimerTask();
                progressTimer.scheduleAtFixedRate(timeTask, 30000, 30000);



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

    //In-app Purchase classes
    private void loadProducts() {
        try {
            mHelper.queryInventoryAsync(true, skuList, new IabHelper.QueryInventoryFinishedListener() {
                @Override
                public void onQueryInventoryFinished(IabResult result, Inventory inv) {
                    items = new ArrayList<>();
                    StickerItem item;

                    for (String s : skuList) {
                        item = new StickerItem();
                        item.setTitle(inv.getSkuDetails(s).getTitle());
                        item.setDescription(inv.getSkuDetails(s).getDescription());
                        item.setPrice(inv.getSkuDetails(s).getPrice());
                        item.setId(inv.getSkuDetails(s).getSku());
                        items.add(item);

                        Toast.makeText(activity, item.toString(), Toast.LENGTH_LONG).show();

                    }

                    setupAdapter();

//                    stickerGridView.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            gridLayoutManager = new GridLayoutManager(activity, 3, LinearLayout.VERTICAL, false);
//                            stickerGridView.setLayoutManager(gridLayoutManager);
//                            adapter = new StickerAdapter(stickerGridView.getContext(),stickers, mHelper, mPurchaseFinishedListener, items);
//                            stickerGridView.setAdapter(adapter);
//                        }
//                    });

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        if(!BuildConfig.APPLICATION_ID.contains("gkeyboard") && !BuildConfig.APPLICATION_ID.contains("lovetok")) {
            switchMenuItem = menu.findItem(R.id.switchicons);
            switchMenuItem.setIcon(withExpressions == false ? R.drawable.shape_trans : R.drawable.shape);
        }
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
                if(!BuildConfig.APPLICATION_ID.contains("gkeyboard") && !BuildConfig.APPLICATION_ID.contains("lovetok")) {
                    Answers.getInstance().logCustom(new CustomEvent("Home Click Event")
                            .putCustomAttribute("Name", "Switch Button"));
                    withExpressions = withExpressions == false ? true : false;
                    prefs.edit().putBoolean("icons_with_text", withExpressions).apply();
                    switchMenuItem.setIcon(withExpressions == false ? R.drawable.shape_trans : R.drawable.shape);
                    stickers = Sticker.initStickers(StickerActivity.this, withExpressions);
                    //adapter = new StickerAdapter(this,stickers,ad)
                    adapter = new StickerAdapter(this,stickers, mHelper, mPurchaseFinishedListener, items, ad);
                    stickerGridView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }

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

//    private void addAdMobView(){
//        ViewGroup adCardView = admobFrame;
//        AdRequest adRequest = new AdRequest.Builder()
//                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
//                .addTestDevice("CCDF3FFB9F1C5F61511338E52C46D7E3")
//                .addTestDevice("8667822E35F5855655D951EA91079F91")// My Galaxy Nexus test phone
//                .build();
//        View view = View.inflate(admobFrame.getContext(), R.layout.admob_sticker_item, adCardView);
//        final NativeExpressAdView mAdView = (NativeExpressAdView) view.findViewById(R.id.adView);
//        mAdView.loadAd(adRequest);
//        mAdView.setVisibility(View.GONE);
//        mAdView.setAdListener(new AdListener(){
//            @Override
//            public void onAdLoaded() {
//                mAdView.setVisibility(View.VISIBLE);
//            }
//
//            @Override
//            public void onAdFailedToLoad(int i) {
//                super.onAdFailedToLoad(i);
//                showNativeAd();
//            }
//        });
//
//        // The NativeExpressAdViewHolder recycled by the RecyclerView may be a different
//        // instance than the one used previously for this position. Clear the
//        // NativeExpressAdViewHolder of any subviews in case it has a different
//        // AdView associated with it, and make sure the AdView for this position doesn't
//        // already have a parent of a different recycled NativeExpressAdViewHolder.
//        if (adCardView.getChildCount() > 0) {
//            adCardView.removeAllViews();
//        }
//        if (mAdView.getParent() != null) {
//            ((ViewGroup) mAdView.getParent()).removeView(mAdView);
//        }
//
//        // Add the Native Express ad to the native express ad view.
//        adCardView.addView(mAdView);
//    }
//
//    private void showNativeAd() {
//        admobFrame.setVisibility(View.GONE);
//        Log.i(TAG, "iN FACEBOOK NATIVE");
//        AdView adView = new AdView(this, "1582301235134013_1582364931794310", AdSize.BANNER_HEIGHT_50);
//        adView.setAdListener(new com.facebook.ads.AdListener() {
//            @Override
//            public void onError(Ad ad, AdError adError) {
//                Log.i(TAG, "ERROR FACEBOOK NATIVE "+ adError.getErrorMessage());
//            }
//
//            @Override
//            public void onAdLoaded(Ad ad) {
//                admobFrame.setVisibility(View.VISIBLE);
//            }
//
//            @Override
//            public void onAdClicked(Ad ad) {
//
//            }
//
//            @Override
//            public void onLoggingImpression(Ad ad) {
//
//            }
//        });
//        admobFrame.addView(adView);
//        adView.loadAd();
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            // not handled, so handle it ourselves (here's where you'd
            // perform any handling of activity results not related to in-app
            // billing...
            super.onActivityResult(requestCode, resultCode, data);
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
                                if(uri != null) {
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
                    Utility.removeStringsFromList("filePaths", context);
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
                            try {
                                // Now you can get rid of this listener
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {

                                    if (viewTreeObserver.isAlive()) {
                                        try {
                                            viewTreeObserver.removeOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                                                @Override
                                                public void onGlobalLayout() {

                                                }
                                            });
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    } else {
                                        viewTreeObserver = getWindow().getDecorView().getViewTreeObserver();
                                        viewTreeObserver.removeGlobalOnLayoutListener(this);
                                    }
                                } else {
                                    if (viewTreeObserver.isAlive()) {
                                        viewTreeObserver.removeGlobalOnLayoutListener(this);
                                    }
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            TextPaint textPaint =  new TextPaint();
                            textPaint.setColor(Color.parseColor("#77554455"));
                            try {
                                showcaseView = new ShowcaseView.Builder(StickerActivity.this)
                                        .setTarget(new ViewTarget(R.id.switchicons, StickerActivity.this))
                                        .setContentText("Tap on this menu to enable or disable stickers with text")
                                        .setContentTitle("Tips")
                                        .withMaterialShowcase()
                                        .setStyle(R.style.CustomShowcaseTheme2)
                                        .hideOnTouchOutside()
                                        .useDecorViewAsParent()
                                        .singleShot(10000)
                                        .build();
//                                showcaseView = new MaterialShowcaseView.Builder(StickerActivity.this)
//                                        .setTarget(switchMenu)
//                                        .setDismissText("GOT IT")
//                                        .setContentText("Tap on this menu to enable or disable stickers with text")
//                                        .setDismissOnTargetTouch(true)
//                                        .setContentTextColor(Color.parseColor("#dddddd"))
//                                        .setDismissTextColor(Color.parseColor("#ffffff"))
//                                        .setDelay(1000) // optional but starting animations immediately in onCreate can make them choppy
//                                        .singleUse(BuildConfig.APPLICATION_ID) // provide a unique ID used to ensure it is only shown once
//                                        .show();
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
                                showcaseView = new ShowcaseView.Builder(StickerActivity.this)
                                        .setTarget(new ViewTarget(R.id.switchicons, StickerActivity.this))
                                        .setContentText("Tap on this menu to enable or disable stickers with text")
                                        .setContentTitle("Tips")
                                        .withMaterialShowcase()
                                        .setStyle(R.style.CustomShowcaseTheme2)
                                        .hideOnTouchOutside()
                                        .useDecorViewAsParent()
                                        .singleShot(10000)
                                        .build();
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
        if(showcaseView != null && showcaseView.isShowing() ){
            try {
                showcaseView.hide();
                showcaseView = null;
            }catch (Exception e){
                super.onBackPressed();
            }
        }else{
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                deleteStickerEmojisOnStorage(StickerActivity.this);
            }
        }else{
            deleteStickerEmojisOnStorage(StickerActivity.this);
        }
    }


    private class ProgressTimerTask extends TimerTask {
        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    randomInt = new Random().nextInt();
                    AdItem ad_t = ads.get( randomInt < 0 ?   (randomInt * -1) % ads.size() : randomInt % ads.size());
                    ad.setTitle(ad_t.getTitle());
                    ad.setAdButtonText(ad_t.getAdButtonText());
                    ad.setDescription(ad_t.getDescription());
                    ad.setImageUrl(ad_t.getImageUrl());
                    ad.setLink(ad_t.getLink());
                    ad.setType(ad_t.getType());
                    adapter.notifyItemChanged(0);
                }
            });
        }
    }

    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        @Override
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            Log.d(TAG, "Purchase finished: " + result + ", purchase: " + purchase);

            // if we were disposed of in the meantime, quit.
            if (mHelper == null) return;

            if (result.isFailure()) {
                Toast.makeText(activity, "Error purchasing: " + result, Toast.LENGTH_SHORT).show();
                return;
            }

            Log.d(TAG, "Purchase successful." + purchase);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean("Purchased", true);
            editor.commit();

            stickerGridView.post(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyDataSetChanged();
                }
            });

            try {
                mHelper.consumeAsync(purchase, mConsumeFinishedListener);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
        @Override
        public void onConsumeFinished(Purchase purchase, IabResult result) {
            Log.d(TAG, "Consumption finished. Purchase: " + purchase + ", result: " + result);

            // if we were disposed of in the meantime, quit.
            if (mHelper == null) return;

            if (result.isSuccess()) {
                Log.d(TAG, "Consumption successful. Provisioning." + purchase.getSku());
            } else {
                Log.d(TAG, "Error while consuming: " + result);
            }
            Log.d(TAG, "End consumption flow.");
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHelper != null) try {
            mHelper.dispose();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mHelper = null;
    }
}