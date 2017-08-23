package keyboard.android.psyphertxt.com.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import keyboard.android.psyphertxt.com.MainSettings;
import keyboard.android.psyphertxt.com.constants.Apple_EmojiIcons;
import keyboard.android.psyphertxt.com.constants.EmojiIcons;
import keyboard.android.psyphertxt.com.constants.Google_EmojiIcons;
import keyboard.android.psyphertxt.com.view.KeyboardSinglePageView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

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
        pages.add(new KeyboardSinglePageView(context, new StaticEmojiAdapter(context, new LinkedList<String>(icons.getCyfaStickerIconIds().keySet()), new LinkedList<Drawable>(icons.getCyfaStickerIconIds().values()))).getView());
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
         return new Google_EmojiIcons(mContext);
    }

    private CharSequence setTitlePage(){
        return "";
    }
}
