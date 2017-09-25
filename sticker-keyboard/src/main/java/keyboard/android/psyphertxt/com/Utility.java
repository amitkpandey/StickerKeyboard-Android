package keyboard.android.psyphertxt.com;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;

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

    public static final LinkedList<Drawable> initArrayList(int resourceName, Context  context) {
        LinkedList<Drawable> list = new LinkedList<>();
        list = new LinkedList<>(initLinkHashMap(resourceName, context).values());
        return list;
    }

    public static final LinkedHashMap<String, Drawable> initLinkHashMap(int resourceName, Context  context) {
        LinkedHashMap<String, Drawable> list = new LinkedHashMap<String, Drawable>();

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
                    String name = context.getResources().getResourceEntryName(icons.getResourceId(i, -1));
                    list.put(name, drawable);
                }
                i++;
            }catch (Exception e){
                e.printStackTrace();
                break;
            }

        }
        return list;
    }

    public static boolean isStoragePermissionGranted(Context context) {
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
                ActivityCompat.requestPermissions((Activity) context, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
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

    public static void removeStringFromList(String key, String filePath, Context context){
        ArrayList<String> list = new ArrayList<>();
        if(getArrayListString(key, context) != null){
            list = getArrayListString(key, context);
        }
        list.remove(filePath);
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
}
