package keyboard.android.psyphertxt.com.adapter;

import android.app.ActivityManager;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
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

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.squareup.picasso.Picasso;

import java.util.LinkedList;
import java.util.List;

import io.fabric.sdk.android.Fabric;
import keyboard.android.psyphertxt.com.EmojiKeyboardService;
import keyboard.android.psyphertxt.com.Utility;


public abstract class BaseEmojiAdapter extends BaseAdapter {

    protected EmojiKeyboardService emojiKeyboardService;
    protected LinkedList<String> emojiTexts;
    protected LinkedList<Integer> iconIds;
    public static EditorInfo info;
    SharedPreferences sharedPrefs;
    private static final String TAG = BaseEmojiAdapter.class.getSimpleName();

    public BaseEmojiAdapter(EmojiKeyboardService emojiKeyboardService) {
        this.emojiKeyboardService = emojiKeyboardService;
        Fabric.with(emojiKeyboardService, new Answers(), new Crashlytics());
    }

    @Override
    public int getCount() {
        return emojiTexts == null ? iconIds.size() : emojiTexts.size();
    }

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        final ImageView imageView;
        sharedPrefs = parent.getContext().getSharedPreferences("Purchased", Context.MODE_PRIVATE);
        if (convertView == null) {
            imageView = new ImageView(emojiKeyboardService);
            int scale = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, emojiKeyboardService.getResources().getDisplayMetrics());
            imageView.setPadding(scale, (int) (scale * 1.2), scale, (int) (scale * 1.2));
            imageView.setAdjustViewBounds(true);
        } else {
            imageView = (ImageView) convertView;
        }

        int resID = imageView.getContext().getResources().getIdentifier(emojiTexts.get(position) , "drawable", imageView.getContext().getPackageName());
        Picasso.with(imageView.getContext())
                .load(resID)
                .into(imageView);
        Log.d(TAG, "showing ");
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if (getCurrentAppPackage(v.getContext()) != null) {
                if(Utility.isStoragePermissionGranted(v.getContext())) {
                    //Sticker Analytics
                    Log.d(TAG, "showing "+String.valueOf(emojiTexts.get(position)).replace("_", " "));
                    Answers.getInstance().logCustom(new CustomEvent("Keyboard Sticker ClickEvent")
                            .putCustomAttribute("Name", String.valueOf(emojiTexts.get(position)).replace("_", " ")));
                    passImage(v.getContext(), imageView, v);
                } else{

                }
            }
            }
        });
        return imageView;
    }

    @Override
    public Object getItem(int arg0) {
        return iconIds.get(arg0);
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

    public void passImage(final Context context, ImageView imageView, final View v) {
        if((BitmapDrawable)((ImageView)imageView).getDrawable() != null) {
            //convert image to bitmap so it can be shared.
            Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
            // Create new bitmap based on the size and config of the old
            Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
            newBitmap.eraseColor(Color.WHITE);
            Canvas canvas = new Canvas(newBitmap);  // create a canvas to draw on the new image
            canvas.drawBitmap(bitmap, 0f, 0f, null); // draw old image on the background
            String bitmapPath = MediaStore.Images.Media.insertImage(context.getContentResolver(), newBitmap, "title", null);
            if (bitmapPath != null) {
                Utility.saveArraylistString("filePaths", bitmapPath, context);
                Uri imageUri = Uri.parse(bitmapPath);
                if (imageUri != null) {
                    Intent intent = createIntent(v.getContext(), imageUri);
                    try {
                        context.getApplicationContext().startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
            //v.getContext().startActivity(intent);
//                    }
//
//                    @Override
//                    public void onError() {
//
//                    }
//                });
        }
    }

    public Intent createIntent(Context context, Uri uri) {
        String appName = getCurrentAppPackage(context);
        Intent sendIntent = new Intent("android.intent.action.MAIN");
        sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
        sendIntent.putExtra("jid", "" + "@s.whatsapp.net");
        sendIntent.putExtra(Intent.EXTRA_TEXT, "");
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.setPackage(appName);
        sendIntent.setType("image/png");
        sendIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return sendIntent;
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