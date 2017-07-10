package keyboard.android.psyphertxt.com.adapter;

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
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import keyboard.android.psyphertxt.com.EmojiKeyboardService;
import keyboard.android.psyphertxt.com.R;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseEmojiAdapter extends BaseAdapter {

    protected EmojiKeyboardService emojiKeyboardService;
    protected ArrayList<String> emojiTexts;
    protected ArrayList<Integer> iconIds;
    public static EditorInfo info;

    public BaseEmojiAdapter(EmojiKeyboardService emojiKeyboardService) {
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
            imageView.setPadding(scale, (int) (scale * 1.2), scale, (int) (scale * 1.2));
            imageView.setAdjustViewBounds(true);
        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setImageResource(iconIds.get(position));
        imageView.setBackgroundResource(R.drawable.btn_background);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (emojiTexts == null) {
                    if (getCurrentAppPackage(v.getContext()) != null) {
                        passImage(v.getContext(), iconIds.get(position), v);
                    }
                } else {
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
        if (info != null && info.packageName != null) {
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
        System.out.println(">>>>>>>>>>>>" + drawableId);
        Picasso.with(v.getContext())
                .load(drawableId)
                .noFade()
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {

                        //convert image to bitmap so it can be shared.
                        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                        String bitmapPath = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "title", null);
                        Uri imageUri = Uri.parse(bitmapPath);
                        Intent intent = createIntent(v.getContext(), imageUri);
                        try {
                            context.getApplicationContext().startActivity(intent);
                        } catch (ActivityNotFoundException e) {
                            e.printStackTrace();
                        }
                        //v.getContext().startActivity(intent);
                    }

                    @Override
                    public void onError() {

                    }
                });
    }

    public Intent createIntent(Context context, Uri uri) {
        Intent sendIntent = new Intent("android.intent.action.MAIN");
        sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
        sendIntent.putExtra("jid", "" + "@s.whatsapp.net");
        sendIntent.putExtra(Intent.EXTRA_TEXT, "");
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.setPackage(getCurrentAppPackage(context));
        sendIntent.setType("image/png");
        sendIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return sendIntent;
//        String packageNames = getCurrentAppPackage(context);
//        System.out.println(">>>>>>>>>>> "+ packageNames);
//        Uri mUri = Uri.parse("smsto:+233543951604");
//        Intent sharingIntent = new Intent(Intent.ACTION_SENDTO, mUri);
//        sharingIntent.setType("image/png");
//        sharingIntent.setPackage(packageNames);
//        sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);
//        sharingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//        return sharingIntent;
    }

    public static final Uri getUriToDrawable(@NonNull Context context,
                                             @AnyRes int drawableId, View v) {
        Uri imageUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                "://" + context.getResources().getResourcePackageName(drawableId)
                + '/' + context.getResources().getResourceTypeName(drawableId)
                + '/' + context.getResources().getResourceEntryName(drawableId));
        return imageUri;
    }
}