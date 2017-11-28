package keyboard.android.psyphertxt.com.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;

import keyboard.android.psyphertxt.com.EmojiKeyboardService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

public class StaticEmojiAdapter extends BaseEmojiAdapter {

    public StaticEmojiAdapter(Context context, LinkedList<String> emojiTextsAsStrings, LinkedList<Integer> iconIds) {
        super((EmojiKeyboardService) context);
        if(emojiTextsAsStrings != null)
            this.emojiTexts =  emojiTextsAsStrings;
        this.iconIds = iconIds;
    }
}