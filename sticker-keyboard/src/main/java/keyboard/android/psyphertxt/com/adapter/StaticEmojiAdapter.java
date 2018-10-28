package keyboard.android.psyphertxt.com.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;

import keyboard.android.psyphertxt.com.EmojiKeyboardService;
import keyboard.android.psyphertxt.com.R;
import keyboard.android.psyphertxt.com.Utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

public class StaticEmojiAdapter extends BaseEmojiAdapter {

    public StaticEmojiAdapter(Context context, LinkedList<String> emojiTextsAsStrings, LinkedList<Integer> iconIds) {
        super((EmojiKeyboardService) context);
//        SharedPreferences prefs = context.getSharedPreferences("Purchased", Context.MODE_PRIVATE);
//        if(!prefs.getBoolean("Purchased", false)){
//            for(Integer drawable : Utility.initArrayList(R.array.drawables_paid, context)){
//                if(drawable != -1) {
//                    if(iconIds.contains(drawable)) {
//                        iconIds.remove(drawable);
//                    }
//                }
//            }
//        }
        if(emojiTextsAsStrings != null)
            this.emojiTexts =  emojiTextsAsStrings;
        this.iconIds = iconIds;
    }
}