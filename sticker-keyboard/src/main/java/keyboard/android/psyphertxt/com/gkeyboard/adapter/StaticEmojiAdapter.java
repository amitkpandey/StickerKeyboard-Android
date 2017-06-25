package keyboard.android.psyphertxt.com.gkeyboard.adapter;

import android.content.Context;

import keyboard.android.psyphertxt.com.gkeyboard.EmojiKeyboardService;

import java.util.ArrayList;
import java.util.Arrays;

public class StaticEmojiAdapter extends BaseEmojiAdapter {

    public StaticEmojiAdapter(Context context, String[] emojiTextsAsStrings, ArrayList<Integer> iconIds) {
        super((EmojiKeyboardService) context);
        if(emojiTextsAsStrings != null)
            this.emojiTexts =  new ArrayList<String>(Arrays.asList(emojiTextsAsStrings));
        this.iconIds = iconIds;
    }
}