package keyboard.android.psyphertxt.com.stickers;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import keyboard.android.psyphertxt.com.constants.EmojiIcons;
import keyboard.android.psyphertxt.com.constants.Sticker_EmojiIcons;

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

    static List<Sticker> initStickers(Context context, boolean withExpressions) {

        EmojiIcons icons = getPreferedIconSet(context, withExpressions);

        List<Sticker> stickers = new ArrayList<>();

        LinkedHashMap<String, Drawable> stickersArray = icons.getCyfaStickerIconIds();

        try {
            for (Map.Entry<String, Drawable> entry : stickersArray.entrySet()) {

                //create instance of sticker model
                Sticker sticker = new Sticker();

                //assign the name of the from xml
                sticker.setName(entry.getKey().replace("_", " "));

                Log.d(TAG, sticker.getName());

                sticker.setDrawable(entry.getValue());
                //sticker.setDrawable(context.getDrawable(stickerName));
                //set image uri using android asset location
                //all images are in png
                //sticker.setUri("file:///android_asset/stickers/" + stickerName + ".png");

                //add stickers to a list so they can be retrieved.
                stickers.add(sticker);
            }
        }catch (Exception e){
            stickers = null;
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

    private static EmojiIcons getPreferedIconSet(Context context, boolean withExpressions) {
        return new Sticker_EmojiIcons(context, withExpressions);
    }

}
