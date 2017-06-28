package keyboard.android.psyphertxt.com.gkeyboard.adapter;

import android.app.ActivityManager;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.AnyRes;
import android.support.annotation.NonNull;
import android.support.annotation.RawRes;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import keyboard.android.psyphertxt.com.gkeyboard.EmojiKeyboardService;
import keyboard.android.psyphertxt.com.gkeyboard.R;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseEmojiAdapter extends BaseAdapter {

    protected EmojiKeyboardService emojiKeyboardService;
    protected ArrayList<String> emojiTexts;
    protected ArrayList<Integer> iconIds;
    public static EditorInfo info;

    public BaseEmojiAdapter(EmojiKeyboardService emojiKeyboardService ) {
        this.emojiKeyboardService = emojiKeyboardService;
    }

    @Override
    public int getCount() {
        return emojiTexts == null ? iconIds.size() : emojiTexts.size();
    }

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        final ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(emojiKeyboardService);
            int scale = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, emojiKeyboardService.getResources().getDisplayMetrics());
            imageView.setPadding(scale, (int)(scale*1.2), scale, (int)(scale * 1.2));
            imageView.setAdjustViewBounds(true);
        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setImageResource(iconIds.get(position));
        imageView.setBackgroundResource(R.drawable.btn_background);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(emojiTexts == null){
                    if(getCurrentAppPackage(v.getContext()) != null ){
                        passImage(v.getContext(), iconIds.get(position), v);
                    }
                }else {
                    emojiKeyboardService.sendText(emojiTexts.get(position));
                }
            }
        });
        return imageView;
    }

    @Override
    public Object getItem(int arg0) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public String getCurrentAppPackage(Context context) {
        info = emojiKeyboardService.getCurrentInputEditorInfo();
        if(info != null && info.packageName != null) {
            return info.packageName;
        }
        final PackageManager pm = context.getPackageManager();
        //Get the Activity Manager Object
        ActivityManager aManager =
                (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //Get the list of running Applications
        List<ActivityManager.RunningAppProcessInfo> rapInfoList = aManager.getRunningAppProcesses();
        //Iterate all running apps to get their details
        for (ActivityManager.RunningAppProcessInfo rapInfo : rapInfoList) {
            //error getting package name for this process so move on
            if (rapInfo.pkgList.length == 0) {
                Log.i("DISCARDED PACKAGE", rapInfo.processName);
                continue;
            }
            try {
                PackageInfo pkgInfo = pm.getPackageInfo(rapInfo.pkgList[0], PackageManager.GET_ACTIVITIES);
                Log.d("EMOJI", pkgInfo.packageName);
                return pkgInfo.packageName;
            } catch (PackageManager.NameNotFoundException e) {
                // Keep iterating
            }
        }
        return null;
    }

    public void passImage(final Context context, int drawableId, final View v) {
        final ImageView imageView = new ImageView(v.getContext());
        System.out.println(">>>>>>>>>>>>"+"file:///android_asset/stickers/"+context.getResources().getResourceEntryName(drawableId).replace("_", " ")+ ".png");
        Picasso.with(v.getContext())
                .load("file:///android_asset/stickers/"+context.getResources().getResourceEntryName(drawableId).replace("_", " ")+ ".png")
                .noFade()
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {

                        //convert image to bitmap so it can be shared.
                        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                        String bitmapPath = MediaStore.Images.Media.insertImage(context.getContentResolver(),bitmap,"title",null);
                        Uri imageUri = Uri.parse(bitmapPath);
                        EmojiKeyboardService.commitPNGImage(imageUri, "", info, emojiKeyboardService.getCurrentInputConnection());
                        //EmojiKeyboardService.doCommitContent("", EmojiKeyboardService.MIME_TYPE_PNG, imageUri,info, v.getContext(), emojiKeyboardService.getCurrentInputConnection());
                    }

                    @Override
                    public void onError() {

                    }
                });
    }

    private static File getFileForResource(
            @NonNull Context context, @RawRes int res, @NonNull File outputDir,
            @NonNull String filename) {
        final File outputFile = new File(outputDir, filename);
        final byte[] buffer = new byte[4096];
        InputStream resourceReader = null;
        try {
            try {
                resourceReader = context.getResources().openRawResource(res);
                OutputStream dataWriter = null;
                try {
                    dataWriter = new FileOutputStream(outputFile);
                    while (true) {
                        final int numRead = resourceReader.read(buffer);
                        if (numRead <= 0) {
                            break;
                        }
                        dataWriter.write(buffer, 0, numRead);
                    }
                    return outputFile;
                } finally {
                    if (dataWriter != null) {
                        dataWriter.flush();
                        dataWriter.close();
                    }
                }
            } finally {
                if (resourceReader != null) {
                    resourceReader.close();
                }
            }
        } catch (IOException e) {
            return null;
        }
    }

}
