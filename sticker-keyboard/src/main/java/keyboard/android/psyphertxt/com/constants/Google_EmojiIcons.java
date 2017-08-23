package keyboard.android.psyphertxt.com.constants;

import android.content.Context;

import keyboard.android.psyphertxt.com.BuildConfig;
import keyboard.android.psyphertxt.com.R;
import keyboard.android.psyphertxt.com.Utility;

public final class Google_EmojiIcons extends EmojiIcons {
    public Google_EmojiIcons(Context context){
        cyfaStickerIconIds = Utility.initLinkHashMap(R.array.drawables, context);
    }
}