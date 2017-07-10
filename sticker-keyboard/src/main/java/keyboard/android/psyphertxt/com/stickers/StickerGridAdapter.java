package keyboard.android.psyphertxt.com.stickers;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.NativeExpressAdView;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.ButterKnife;
import keyboard.android.psyphertxt.com.R;

class StickerGridAdapter extends ArrayAdapter<Sticker> {

    private static final String TAG = StickerGridAdapter.class.getSimpleName();
    private Context context;
    private List<Sticker> stickers;

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

        holder.imageStickers.setImageDrawable(stickers.get(position).getDrawable());

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

    private void stickerOnClickEvent(View view, final int position){
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (StickerFirstTimeActivity.isStoragePermissionGranted(v.getContext())) {
                    Log.v(TAG,"Permission is granted");
                    //get current sticker by item position
                    final Sticker sticker = stickers.get(position);

                    //convert image to bitmap so it can be shared.
                    Bitmap bitmap = ((BitmapDrawable) sticker.getDrawable()).getBitmap();
                    shareStickerImage(sticker, bitmap, v.getContext());

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
                final Bitmap bitmap = ((BitmapDrawable) sticker.getDrawable()).getBitmap();
                MaterialDialog materialDialog =   new MaterialDialog.Builder(view.getContext())
                        .title(R.string.app_name)
                        .customView(R.layout.sticker_dialog_view, wrapInScrollView)
                        .positiveText("Share")
                        .neutralText("Close")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                shareStickerImage(sticker, bitmap, v.getContext());
                                dialog.dismiss();
                            }
                        })
                        .build();

                materialDialog.show();

                ImageView imageView = ButterKnife.findById(materialDialog, R.id.imageView);
                imageView.setImageBitmap(bitmap);
                AdRequest adRequest = new AdRequest.Builder()
                        .addTestDevice("CCDF3FFB9F1C5F61511338E52C46D7E3")
                        .build();
                final NativeExpressAdView mAdView = ButterKnife.findById(materialDialog, R.id.adView);;
                mAdView.loadAd(adRequest);
                return true;
            }
        });
    }



    private void shareStickerImage(Sticker sticker,Bitmap bitmap, Context context) {

        String bitmapPath = MediaStore.Images.Media.insertImage(context.getContentResolver(),bitmap,"title",null);
        Uri imageUri = Uri.parse(bitmapPath);
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, sticker.getName());
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        shareIntent.setType("image/*");
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(Intent.createChooser(shareIntent, "Share Sticker"));
    }

}
