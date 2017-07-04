package keyboard.android.psyphertxt.com.gkeyboard.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import keyboard.android.psyphertxt.com.gkeyboard.MainSettings;
import keyboard.android.psyphertxt.com.gkeyboard.constants.Apple_EmojiIcons;
import keyboard.android.psyphertxt.com.gkeyboard.constants.EmojiIcons;
import keyboard.android.psyphertxt.com.gkeyboard.constants.Google_EmojiIcons;
import keyboard.android.psyphertxt.com.gkeyboard.view.KeyboardSinglePageView;

import java.util.ArrayList;

public class EmojiPagerAdapter extends PagerAdapter {

    private final String[] TITLES = {"cyfa"};

    private ViewPager pager;
    private ArrayList<View> pages;
    private int keyboardHeight;
    private Context mContext;

    public EmojiPagerAdapter(Context context, ViewPager pager, int keyboardHeight) {
        super();

        this.pager = pager;
        mContext = context;
        this.keyboardHeight = keyboardHeight;
        this.pages = new ArrayList<View>();

        EmojiIcons icons = getPreferedIconSet();
        //pages.add(new KeyboardSinglePageView(context, new RecentEmojiAdapter(context)).getView());
        pages.add(new KeyboardSinglePageView(context, new StaticEmojiAdapter(context, null, icons.getCyfaStickerIconIds())).getView());
    }

    @Override
    public View instantiateItem(ViewGroup container, int position) {
        pager.addView(pages.get(position), position, keyboardHeight);
        return pages.get(position);
    }

    @Override
    public void destroyItem (ViewGroup container, int position, Object object) {
        pager.removeView(pages.get(position));
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return setTitlePage();
    }

    @Override
    public int getCount() {
        return TITLES.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    private EmojiIcons getPreferedIconSet() {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(pager.getContext());

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

    private CharSequence setTitlePage(){
        return "";
    }
}
