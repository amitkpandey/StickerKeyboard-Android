package keyboard.android.psyphertxt.com.stickers;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import keyboard.android.psyphertxt.com.R;
import keyboard.android.psyphertxt.com.ShareBroadcastReceiver;
import keyboard.android.psyphertxt.com.Utility;
import keyboard.android.psyphertxt.com.products.IabHelper;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

class StickerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = StickerGridAdapter.class.getSimpleName();
    IabHelper.OnIabPurchaseFinishedListener purchaseFinishedListener;
    IabHelper helper;
    private Context context;
    private List<Sticker> stickers;
    List<StickerItem> items;
    SharedPreferences sharedPrefs;
    AdItem ad;


    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageStickers, imageLock;

        public ViewHolder(View view) {
            super(view);
            imageStickers = view.findViewById(R.id.sticker_image);
            imageLock = view.findViewById(R.id.sticker_lock);
        }
    }


    public class AdViewHolder extends RecyclerView.ViewHolder {
        ImageView adImage;
        TextView adDescription;
        Button adCallToAction;

        public AdViewHolder(View view) {
            super(view);
            adImage = view.findViewById(R.id.adImage);
            adDescription = view.findViewById(R.id.adDescription);
            adCallToAction = view.findViewById(R.id.adCallToAction);
        }
    }

    StickerAdapter(Context context, List<Sticker> stickers, AdItem ad) {
        this.context = context;
        this.stickers = stickers;
        this.ad = ad;
    }

    StickerAdapter(Context context, List<Sticker> stickers, IabHelper helper, IabHelper.OnIabPurchaseFinishedListener purchaseFinishedListener, List<StickerItem> items) {
        this.context = context;
        this.stickers = stickers;
        this.helper = helper;
        this.purchaseFinishedListener = purchaseFinishedListener;
        this.items = items;
    }


        @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == 0){
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.ad_layout, parent, false);
            return new AdViewHolder(itemView);
        }else{
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.sticker_view, parent, false);
            sharedPrefs = context.getSharedPreferences("Purchased", Context.MODE_PRIVATE);

            return new ViewHolder(itemView);
        }

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if(holder.getItemViewType() == 0){
            if(ad != null) {
                holder.itemView.setVisibility(View.VISIBLE);
                Picasso.with(context)
                          .load((int)ad.getImageUrl())
                        .into(((AdViewHolder) holder).adImage);
                ((AdViewHolder) holder).adDescription.setText(Html.fromHtml("<b>"+ad.getTitle()+"</b> "+ad.getDescription()));
                ((AdViewHolder) holder).adCallToAction.setText(ad.getAdButtonText());



                ((AdViewHolder) holder).adCallToAction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Answers.getInstance().logCustom(new CustomEvent("Ad Call To Action")
                                .putCustomAttribute("Link", ad.getLink()).putCustomAttribute("Type", ad.getAdButtonText()).putCustomAttribute("Name", ad.getTitle()));

                        if(ad.getType() == AdItem.ADTYPE.ANDROID  || ad.getType() == AdItem.ADTYPE.WEB){
                            try {
                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setData(Uri.parse(ad.getLink()));
                                holder.itemView.getContext().startActivity(i);
                            }catch (Exception e){
                                    e.printStackTrace();
                                    Toast.makeText(holder.itemView.getContext(), "No App found to handle Web Launches", Toast.LENGTH_LONG).show();
                            }
                        } else if(ad.getType() == AdItem.ADTYPE.PHONE){
                            try{
                                Intent intent = new Intent(Intent.ACTION_DIAL);
                                intent.setData(Uri.parse("tel:"+ad.getLink()));
                                holder.itemView.getContext().startActivity(intent);
                            }catch (Exception e){
                                e.printStackTrace();
                                Toast.makeText(holder.itemView.getContext(), "No App found to handle Web Launches", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });

            }else{
                holder.itemView.setVisibility(View.GONE);
            }

        }else {
            System.out.println(">>>>>>>>>>" + stickers.get(position).getName());
            int resID = context.getResources().getIdentifier(stickers.get(position).getName(), "drawable", context.getPackageName());
            if (resID != 0) {
                try {
                    Picasso.with(context)
                            .load(resID)
                            .into(((ViewHolder) holder).imageStickers);
                    stickerOnClickEvent(((ViewHolder) holder).imageStickers, position);
                    stickerItemOnLongPressEvent(((ViewHolder) holder).imageStickers, position);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            String stickerName = stickers.get(position).getName();
            if (!(sharedPrefs.getBoolean("Purchased", false)) ){
                ((ViewHolder) holder).imageLock.post(new Runnable() {
                    @Override
                    public void run() {
                        ((ViewHolder) holder).imageLock.setVisibility(View.GONE);
                    }
                });
            } else {
                if (!(stickerName.equals("kpa") || stickerName.equals("mumu") || stickerName.equals("chop_kiss_no_text")
                || stickerName.equals("baff_up_2_notxt") || stickerName.equals("i_don_die_notxt") || stickerName.equals("congrats"))) {
                    ((ViewHolder) holder).imageLock.post(new Runnable() {
                        @Override
                        public void run() {
                            ((ViewHolder) holder).imageLock.setVisibility(View.VISIBLE);
                        }
                    });
                } else {
                    ((ViewHolder) holder).imageLock.post(new Runnable() {
                        @Override
                        public void run() {
                            ((ViewHolder) holder).imageLock.setVisibility(View.GONE);
                        }
                    });
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        int count = 0;
        if (stickers != null){
            if(!stickers.isEmpty()){
                count = stickers.size();
            }
        }
        return count;
    }

    void refill(List<Sticker> stickers) {
        this.stickers.clear();
        this.stickers.addAll(stickers);
        notifyDataSetChanged();

    }


//    private void setupAdapter(Context context, List stickers, GridView stickerGridView) {
//
//        if (stickerGridView.getAdapter() == null) {
//            StickerGridAdapter adapter = new StickerGridAdapter(context, stickers);
//            stickerGridView.setAdapter(adapter);
//        } else {
//            ((StickerGridAdapter) stickerGridView.getAdapter()).refill(stickers);
//        }
//    }


    private void stickerOnClickEvent(final View view, final int position){
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (Utility.isStoragePermissionGranted(v.getContext())) {
                    Log.v(TAG,"Permission is granted");
                    //get current sticker by item position
                    final Sticker sticker = stickers.get(position);

                    Log.d(TAG, sticker.getName());
                    //Sticker Analytics
                    Answers.getInstance().logCustom(new CustomEvent("Sticker ClickEvent")
                            .putCustomAttribute("Name", sticker.getName()));

                    //convert image to bitmap so it can be shared.
                    try {
                        if(((ImageView) view).getDrawable() != null) {
                            Bitmap bitmap = ((BitmapDrawable) ((ImageView) view).getDrawable()).getBitmap();
                            processImage(bitmap,sticker);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
            }
        });
    }

    private void stickerItemOnLongPressEvent(final View view, final int position) {
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View v) {
                boolean wrapInScrollView = true;
                final Sticker sticker = stickers.get(position);
                Log.d(TAG, sticker.getName());

                //Sticker Analytics
                Answers.getInstance().logCustom(new CustomEvent("Sticker LongPressEvent")
                        .putCustomAttribute("Name", sticker.getName()));

                if((BitmapDrawable)((ImageView)view).getDrawable() != null) {
                    final Bitmap bitmap = ((BitmapDrawable) ((ImageView) view).getDrawable()).getBitmap();
                    MaterialDialog materialDialog = new MaterialDialog.Builder(view.getContext())
                            .title(R.string.app_name)
                            .customView(R.layout.sticker_dialog_view, wrapInScrollView)
                            .positiveText("Share")
                            .neutralText("Close")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    processImage(bitmap,sticker);
                                    dialog.dismiss();
                                }
                            })
                            .build();
                    materialDialog.show();

                    //admobFrame = ButterKnife.findById(materialDialog, R.id.admob_frame);
                    ImageView imageView = ButterKnife.findById(materialDialog, R.id.imageView);
                    imageView.setImageBitmap(bitmap);
//                    AdRequest adRequest = new AdRequest.Builder()
//                            .addTestDevice("CCDF3FFB9F1C5F61511338E52C46D7E3")
//                            .build();
//                    final NativeExpressAdView mAdView = ButterKnife.findById(materialDialog, R.id.adView);
//                    mAdView.setVisibility(View.GONE);
//                    mAdView.setAdListener(new AdListener() {
//                        @Override
//                        public void onAdLoaded() {
//                            super.onAdLoaded();
//                            mAdView.setVisibility(View.VISIBLE);
//                        }
//
//                        @Override
//                        public void onAdFailedToLoad(int i) {
//                            super.onAdFailedToLoad(i);
//                            showNativeAd();
//
//                        }
//                    });
//                    mAdView.loadAd(adRequest);
                }
                return true;
            }
        });
    }

    private void shareStickerImage(Bitmap bitmap, Context context) {

        if(Utility.isStoragePermissionGranted(context)) {
            fixMediaDir();
            String bitmapPath = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "title", null);
            Utility.saveArraylistString("filePaths", bitmapPath, context);
            Uri imageUri = Uri.parse(bitmapPath);
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
            shareIntent.setType("image/*");
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
                Log.d("INFORMATION", "The current android version allow us to know what app is chosen by the user.");

                Intent receiverIntent = new Intent(context,ShareBroadcastReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, receiverIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                shareIntent = Intent.createChooser(shareIntent,"Share via...", pendingIntent.getIntentSender());
            }
            ((Activity)context).startActivityForResult(shareIntent, 5);
        }
    }


    private void processImage(Bitmap bitmap, Sticker sticker) {

        if(sharedPrefs.getBoolean("Purchase",true)){
            try {
                // Create new bitmap based on the size and config of the old
                Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
                newBitmap.eraseColor(Color.WHITE);
                Canvas canvas = new Canvas(newBitmap);  // create a canvas to draw on the new image
                canvas.drawBitmap(bitmap, 0f, 0f, null); // draw old image on the background
                //bitmap.recycle();
                shareStickerImage(newBitmap, context);
            } catch (Exception exception) {
                Log.e("debug_log", exception.toString());
            }
        }else {
            if(sticker.getName().equals("kpa") || sticker.getName().equals("mumu") || sticker.getName().equals("chop_kiss_no_text")
                    || sticker.getName().equals("baff_up_2_notxt") || sticker.getName().equals("i_don_die_notxt") || sticker.getName().equals("congrats")){
                try {
                    // Create new bitmap based on the size and config of the old
                    Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
                    newBitmap.eraseColor(Color.WHITE);
                    Canvas canvas = new Canvas(newBitmap);  // create a canvas to draw on the new image
                    canvas.drawBitmap(bitmap, 0f, 0f, null); // draw old image on the background
                    //bitmap.recycle();
                    shareStickerImage(newBitmap, context);
                } catch (Exception exception) {
                    Log.e("debug_log", exception.toString());
                }
            }else
            {
                helper.launchPurchaseFlow((Activity) context, context.getString(R.string.purchase_itemId),1001, purchaseFinishedListener);
            }
        }
    }

//    private void showNativeAd() {
//        admobFrame.setVisibility(View.GONE);
//        Log.i(TAG, "iN FACEBOOK NATIVE");
//        AdView adView = new AdView(admobFrame.getContext(), "1582301235134013_1582364931794310", com.facebook.ads.AdSize.BANNER_HEIGHT_50);
//        adView.setAdListener(new com.facebook.ads.AdListener() {
//            @Override
//            public void onError(Ad ad, AdError adError) {
//
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

    void fixMediaDir() {
        File sdcard = Environment.getExternalStorageDirectory();
        if (sdcard != null) {
            File mediaDir = new File(sdcard, "DCIM/Camera");
            if (!mediaDir.exists()) {
                mediaDir.mkdirs();
            }
            mediaDir = new File(sdcard, "Pictures");
            if (!mediaDir.exists()) {
                mediaDir.mkdirs();
            }
            //new File("/sdcard/Pictures").mkdirs();
        }
    }

    @Override
    public int getItemViewType(int position) {
        super.getItemViewType(position);
        if(position == 0){
            return 0;
        }
        return 1;
    }
}
