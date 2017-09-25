package keyboard.android.psyphertxt.com.constants;

import android.content.Context;

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
    }
}