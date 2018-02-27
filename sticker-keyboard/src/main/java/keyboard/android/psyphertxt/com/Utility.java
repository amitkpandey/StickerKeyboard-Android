package keyboard.android.psyphertxt.com;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.app.AlertDialog;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import keyboard.android.psyphertxt.com.utilities.ObjectSerializer;


public class Utility {
    private static final String TAG = Utility.class.getSimpleName();

    public static final ArrayList<Integer> initArrayList(int... ints) {
        ArrayList<Integer> list = new ArrayList<Integer>();
        for (int i : ints)
        {
            list.add(i);
        }
        return list;
    }

    public static final LinkedList<Integer> initArrayList(int resourceName, Context  context) {
        LinkedList<Integer> list = new LinkedList<>();
        list = new LinkedList<>(initLinkHashMap(resourceName, context).values());
        return list;
    }

    public static final LinkedHashMap<String, Integer> initLinkHashMap(int resourceName, Context  context) {
        LinkedHashMap<String, Integer> list = new LinkedHashMap<String, Integer>();

        Resources res = context.getResources();
        TypedArray icons = res.obtainTypedArray(resourceName);
        int i = 0;
        while (true)
        {
            try {
                Log.d("<<<<", String.valueOf(i));
                Drawable drawable = icons.getDrawable(i);
                if (drawable == null) {
                    break;
                } else {
                    int drawableId = icons.getResourceId(i, -1);
                    if(drawableId != -1) {
                        String name = context.getResources().getResourceEntryName(icons.getResourceId(i, -1));
                        System.out.println(">>>>>>>>>"+name);
                        list.put(name, drawableId);
                    }
                }
                i++;
            }catch (Exception e){
                e.printStackTrace();
                break;
            }

        }
        return list;
    }

    public static boolean isStoragePermissionGranted(final Context context) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (context.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Answers.getInstance().logCustom(new CustomEvent("First Launch Permission Check Event")
                        .putCustomAttribute("Name", "Permission Already Granted"));
                Log.v(TAG,"Permission is granted");
                return true;
            } else {
                Answers.getInstance().logCustom(new CustomEvent("First Launch Permission Check Event")
                        .putCustomAttribute("Name", "Permission Already Denied"));
                Log.v(TAG,"Permission is revoked");
                if(context instanceof Activity) {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                }else if(context instanceof EmojiKeyboardService){

                    try {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context.getApplicationContext());
                        builder.setTitle("Storage Permission");
                        builder.setMessage("You haven't enabled the storage permission. Please enable to share stickers");
                        builder.setNegativeButton("DISABLE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).setPositiveButton("ENABLE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID));
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent);
                            }
                        });
                        AlertDialog alert = builder.create();
                        Window window = alert.getWindow();
                        WindowManager.LayoutParams lp = window.getAttributes();
                        lp.token = EmojiKeyboardService.mInputView.getWindowToken();
                        lp.type = WindowManager.LayoutParams.TYPE_APPLICATION_ATTACHED_DIALOG;
                        window.setAttributes(lp);
                        window.addFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
                        alert.show();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted");
            Answers.getInstance().logCustom(new CustomEvent("First Launch Event")
                    .putCustomAttribute("Name", "Permission Already Granted <23"));
            return true;
        }
    }


    public static void saveArraylistString(String key, String filePath, Context context){
        ArrayList<String> list = new ArrayList<>();
        if(getArrayListString(key, context) != null){
            list = getArrayListString(key, context);
        }
        list.add(filePath);
        //save list into SP
        SharedPreferences prefs = context.getSharedPreferences("gh", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        try {
            editor.putString(key, ObjectSerializer.serialize(list));
        } catch (Exception e) {
            e.printStackTrace();
        }
        editor.commit();
    }

    public static void removeStringsFromList(String key, Context context){
        ArrayList<String> list = new ArrayList<>();
        //save list into SP
        SharedPreferences prefs = context.getSharedPreferences("gh", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        try {
            editor.putString(key, ObjectSerializer.serialize(list));
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d(TAG, list.toString());
        editor.commit();
    }


    public static ArrayList<String> getArrayListString(String key, Context context) {
        // load list from preference
        SharedPreferences prefs = context.getSharedPreferences("gh", Context.MODE_PRIVATE);
        ArrayList<String> strings = new ArrayList<String>();
        try {
            strings = (ArrayList<String>) ObjectSerializer.deserialize(prefs.getString(key, ObjectSerializer.serialize(new ArrayList<String>())));
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d(TAG, strings.toString());
        return strings;
    }

    public static Map<String, String> splitQuery(String urlString) {
        try {
            Map<String, String> query_pairs = new LinkedHashMap<String, String>();
            URL url = null;
            try {
                url = new URL(urlString);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            if (url != null) {
                String query = url.getQuery();
                if (query != null) {
                    if (query.contains("&")) {
                        String[] pairs = query.split("&");
                        for (String pair : pairs) {
                            int idx = pair.indexOf("=");
                            try {
                                query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
            return query_pairs;
        } catch (Exception e){
            e.printStackTrace();
        }
        return new LinkedHashMap<>();
    }
}
