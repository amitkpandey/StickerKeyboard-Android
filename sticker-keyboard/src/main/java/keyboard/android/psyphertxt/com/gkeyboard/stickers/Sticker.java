package keyboard.android.psyphertxt.com.gkeyboard.stickers;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import keyboard.android.psyphertxt.com.gkeyboard.R;

class Sticker {
    private String name;
    private String uri;

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

        List<Sticker> stickers = new ArrayList<>();

        String[] stickersArray = context.getResources().getStringArray(R.array.stickers);

        for (String stickerName : stickersArray) {

            //create instance of sticker model
            Sticker sticker = new Sticker();

            //assign the name of the from xml
            sticker.setName(stickerName);

            //set image uri using android asset location
            //all images are in png
            sticker.setUri("file:///android_asset/stickers/" + stickerName + ".png");

            //add stickers to a list so they can be retrieved.
            stickers.add(sticker);
        }

        return stickers;
    }

    @Override
    public String toString() {
        return name;
    }
}
