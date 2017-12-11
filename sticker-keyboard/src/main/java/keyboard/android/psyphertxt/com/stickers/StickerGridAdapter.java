package keyboard.android.psyphertxt.com.stickers;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.NativeExpressAdView;
import com.squareup.picasso.Picasso;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import butterknife.ButterKnife;
import keyboard.android.psyphertxt.com.R;
import keyboard.android.psyphertxt.com.ShareBroadcastReceiver;
import keyboard.android.psyphertxt.com.Utility;

class StickerGridAdapter extends ArrayAdapter<Sticker> {

    private static final String TAG = StickerGridAdapter.class.getSimpleName();
    private Context context;
    private List<Sticker> stickers;
    LinearLayout admobFrame;

    StickerGridAdapter(Context context, List<Sticker> stickers) {
        super(context, R.layout.sticker_view, stickers);
        this.context = context;
        this.stickers = stickers;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.sticker_view, parent, false);
            holder = new ViewHolder();
            holder.imageStickers = (ImageView) convertView.findViewById(R.id.sticker_image);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        int resID = context.getResources().getIdentifier(stickers.get(position).getName() , "drawable", context.getPackageName());
        if(resID != 0) {
            try {
                Picasso.with(context)
                        .load(resID)
                        .into(holder.imageStickers);
                stickerOnClickEvent(holder.imageStickers, position);
                stickerItemOnLongPressEvent(holder.imageStickers, position);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

       // holder.imageStickers.setImageDrawable(stickers.get(position).getDrawable());

        stickerOnClickEvent(convertView, position);
        stickerItemOnLongPressEvent(convertView, position);

        return convertView;
    }


    private class ViewHolder {
        ImageView imageStickers;
    }

    void refill(List<Sticker> stickers) {
//        this.stickers.clear();
//        this.stickers.addAll(stickers);
//        notifyDataSetChanged();

    }

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

                    if((BitmapDrawable)((ImageView)view).getDrawable() != null) {
                        //convert image to bitmap so it can be shared.
                        Bitmap bitmap = ((BitmapDrawable) ((ImageView) view).getDrawable()).getBitmap();
                        processImage(bitmap);
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
                                    processImage(bitmap);
                                    dialog.dismiss();
                                }
                            })
                            .build();
                    materialDialog.show();

                    admobFrame = ButterKnife.findById(materialDialog, R.id.admob_frame);
                    ImageView imageView = ButterKnife.findById(materialDialog, R.id.imageView);
                    imageView.setImageBitmap(bitmap);
                    AdRequest adRequest = new AdRequest.Builder()
                            .addTestDevice("CCDF3FFB9F1C5F61511338E52C46D7E3")
                            .build();
                    final NativeExpressAdView mAdView = ButterKnife.findById(materialDialog, R.id.adView);
                    mAdView.setVisibility(View.GONE);
                    mAdView.setAdListener(new AdListener() {
                        @Override
                        public void onAdLoaded() {
                            super.onAdLoaded();
                            mAdView.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAdFailedToLoad(int i) {
                            super.onAdFailedToLoad(i);
                            showNativeAd();

                        }
                    });
                    mAdView.loadAd(adRequest);
                }
                return true;
            }
        });
    }

    private void shareStickerImage(Bitmap bitmap, Context context) {

        if(Utility.isStoragePermissionGranted(context)) {
            fixMediaDir();
        }
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

    private void processImage(Bitmap bitmap) {
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
    }

//    public Bitmap getBitmap(Bitmap bitmap) {
//        Bitmap bmOverlay = Bitmap.createBitmap(bitmap2.getWidth(), bitmap2.getHeight(), bitmap2.getConfig());
//        Canvas canvas = new Canvas(bmOverlay);
//        canvas.drawBitmap(bitmap, matrix, null);
//        canvas.drawBitmap(bitmap2, 0, 0, null);
//        return bmOverlay;
//    }

    private void showNativeAd() {
        admobFrame.setVisibility(View.GONE);
        Log.i(TAG, "iN FACEBOOK NATIVE");
        AdView adView = new AdView(admobFrame.getContext(), "1582301235134013_1582364931794310", com.facebook.ads.AdSize.BANNER_HEIGHT_50);
        adView.setAdListener(new com.facebook.ads.AdListener() {
            @Override
            public void onError(Ad ad, AdError adError) {

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

}
