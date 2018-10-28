package keyboard.android.psyphertxt.com.stickers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import keyboard.android.psyphertxt.com.R;
import keyboard.android.psyphertxt.com.products.IabHelper;
import keyboard.android.psyphertxt.com.products.IabResult;
import keyboard.android.psyphertxt.com.products.Inventory;
import keyboard.android.psyphertxt.com.products.Purchase;

public class PurchaseStickerActivity extends AppCompatActivity {


    private static final String TAG = PurchaseStickerActivity.class.getName();
    IabHelper mHelper;
    RecyclerView rvItems;
    StickerAdapter adapter;
    GridLayoutManager gridLayoutManager;
    List<Sticker> stickers;
    boolean withExpressions = true;
    List<StickerItem> items;
    SharedPreferences prefs;

    // AdItem ad;

    List<String> skuList;
    int RC_REQUEST = 10001;
    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_sticker);
        skuList = Arrays.asList(PurchaseStickerActivity.this.getString(R.string.purchase_itemId));



        activity = PurchaseStickerActivity.this;
        rvItems = findViewById(R.id.rv_Stickers);
        items = new ArrayList<>();
        //initStickers static themes
        stickers = Sticker.initStickers(this, withExpressions);

//        rvItems.post(new Runnable() {
//            @Override
//            public void run() {
//                gridLayoutManager = new GridLayoutManager(activity, 3, LinearLayout.VERTICAL, false);
//                rvItems.setLayoutManager(gridLayoutManager);
//                adapter = new StickerAdapter(rvItems.getContext(),stickers,null);
//                rvItems.setAdapter(adapter);
//            }
//        });

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

       prefs = this.getSharedPreferences("Purchased", Context.MODE_PRIVATE);

    }

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

                    rvItems.post(new Runnable() {
                        @Override
                        public void run() {
                            gridLayoutManager = new GridLayoutManager(activity, 3, LinearLayout.VERTICAL, false);
                            rvItems.setLayoutManager(gridLayoutManager);
                            adapter = new StickerAdapter(rvItems.getContext(),stickers, mHelper, mPurchaseFinishedListener, items);
                            rvItems.setAdapter(adapter);
                        }
                    });

                   /* if (items.isEmpty()) {
                        rvItems.setVisibility(View.GONE);
                        llEmptyList.setVisibility(View.VISIBLE);
                    } else {
                        llEmptyList.setVisibility(View.GONE);
                        rvItems.setVisibility(View.VISIBLE);

                        lm = new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false);
                        rvItems.setLayoutManager(lm);

                        adapter = new ItemAdapter(activity, items);
                        rvItems.setAdapter(adapter);
                    }*/
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            // not handled, so handle it ourselves (here's where you'd
            // perform any handling of activity results not related to in-app
            // billing...
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

  /*  @OnClick(R.id.btnContinue)
    public void clickContinue(View v) {
        PurchaseItem item = adapter.getSelected();

        try {
            mHelper.launchPurchaseFlow(activity, item.getId(), RC_REQUEST, mPurchaseFinishedListener, "");
        } catch (IabHelper.IabAsyncInProgressException e) {
            Log.d(TAG, "onProductClick: " + e.getMessage());
        }
    }*/

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
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("Purchased", true);
            editor.commit();

            rvItems.post(new Runnable() {
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
