package keyboard.android.psyphertxt.com.stickers;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import keyboard.android.psyphertxt.com.MainSettings;
import keyboard.android.psyphertxt.com.constants.Apple_EmojiIcons;
import keyboard.android.psyphertxt.com.constants.EmojiIcons;
import keyboard.android.psyphertxt.com.constants.Google_EmojiIcons;

class Sticker {
    private String name;
    private String uri;
    private Drawable drawable;
    private static final String TAG = Sticker.class.getSimpleName();

    String getUri() {
        return uri;
    }

    private void setUri(String uri) {
        this.uri = uri;
    }

    String getName() {
        return name;
    }

    private void setName(String name) {
        this.name = name;
    }

    static List<Sticker> initStickers(Context context) {

        EmojiIcons icons = getPreferedIconSet(context);

        List<Sticker> stickers = new ArrayList<>();

        ArrayList<Integer> stickersArray = icons.getCyfaStickerIconIds();

        for (Integer stickerName : stickersArray) {

            //create instance of sticker model
            Sticker sticker = new Sticker();

            //assign the name of the from xml
            sticker.setName(String.valueOf(context.getResources().getResourceEntryName(stickerName)).replace("_"," "));

            Log.d(TAG, sticker.getName());

            sticker.setDrawable(ContextCompat.getDrawable(context, stickerName));
            //sticker.setDrawable(context.getDrawable(stickerName));
            //set image uri using android asset location
            //all images are in png
            //sticker.setUri("file:///android_asset/stickers/" + stickerName + ".png");

            //add stickers to a list so they can be retrieved.
            stickers.add(sticker);
        }

        return stickers;
    }

    public Drawable getDrawable() {
        return drawable;
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }

    @Override
    public String toString() {
        return name;
    }

    private static EmojiIcons getPreferedIconSet(Context context) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        if (sharedPreferences
                .getString(MainSettings.CHANGE_ICON_SET_KEY, MainSettings.CHANGE_ICON_SET_VALUE_DEFAULT)
                .equals(MainSettings.CHANGE_ICON_SET_VALUE_GOOGLE)){
            return new Google_EmojiIcons();
        } else if (sharedPreferences
                .getString(MainSettings.CHANGE_ICON_SET_KEY, MainSettings.CHANGE_ICON_SET_VALUE_DEFAULT)
                .equals(MainSettings.CHANGE_ICON_SET_VALUE_APPLE)) {
            return new Apple_EmojiIcons();
        }

        return new Google_EmojiIcons();
    }

}
