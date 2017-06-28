package keyboard.android.psyphertxt.com.gkeyboard.stickers;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import keyboard.android.psyphertxt.com.gkeyboard.R;

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

        Picasso.with(context)
                .load(stickers.get(position).getUri())
                .noFade()
                .into(holder.imageStickers);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (StickerActivity.isStoragePermissionGranted(v.getContext())) {
                    Log.v(TAG,"Permission is granted");
                    //get current sticker by item position
                    final Sticker sticker = stickers.get(position);

                    final ImageView imageView = new ImageView(v.getContext());
                    Picasso.with(v.getContext())
                            .load(stickers.get(position).getUri())
                            .noFade()
                            .into(imageView, new Callback() {
                                @Override
                                public void onSuccess() {

                                    //convert image to bitmap so it can be shared.
                                    Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                                    shareStickerImage(sticker, bitmap, v.getContext());

                                }

                                @Override
                                public void onError() {

                                }
                            });
                }
            }
        });
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
