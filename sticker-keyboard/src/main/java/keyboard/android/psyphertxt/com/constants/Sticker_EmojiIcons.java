package keyboard.android.psyphertxt.com.constants;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;

import keyboard.android.psyphertxt.com.BuildConfig;
import keyboard.android.psyphertxt.com.R;
import keyboard.android.psyphertxt.com.Utility;

public final class Sticker_EmojiIcons extends EmojiIcons {
    public Sticker_EmojiIcons(Context context, boolean withExpressions){
        if(withExpressions) {
            cyfaStickerIconIds = Utility.initLinkHashMap(R.array.drawables, context);
        }else{
            cyfaStickerIconIds = Utility.initLinkHashMap(R.array.drawables_no_expressions, context);
        }
        SharedPreferences prefs = context.getSharedPreferences("Purchased", Context.MODE_PRIVATE);
        if(!prefs.getBoolean("Purchased", false)){
            Resources res = context.getResources();
            TypedArray icons = res.obtainTypedArray(R.array.drawables_paid);
            int i = 0;
            for(Integer drawable : Utility.initArrayList(R.array.drawables_paid, context)){
                if(drawable != -1) {
                    String name = context.getResources().getResourceEntryName(icons.getResourceId(i, -1));
                    if(cyfaStickerIconIds.containsKey(name)) {
                        cyfaStickerIconIds.remove(name);
                    }
                }
                i++;
            }
        }
    }
}