package keyboard.android.psyphertxt.com.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.LinkedList;

import keyboard.android.psyphertxt.com.constants.EmojiIcons;
import keyboard.android.psyphertxt.com.constants.Sticker_EmojiIcons;
import keyboard.android.psyphertxt.com.view.KeyboardSinglePageView;

public class EmojiPagerAdapter extends PagerAdapter {

    private final String[] TITLES = {"cyfa"};

    private ViewPager pager;
    private ArrayList<View> pages;
    private int keyboardHeight;
    private Context mContext;
    boolean withExpression;
    StaticEmojiAdapter adapter;

    public EmojiPagerAdapter(Context context, ViewPager pager, int keyboardHeight, boolean withExpression) {
        super();

        this.pager = pager;
        mContext = context;
        this.keyboardHeight = keyboardHeight;
        this.pages = new ArrayList<View>();
        this.withExpression = withExpression;

        EmojiIcons icons = getPreferedIconSet();
        adapter = new StaticEmojiAdapter(context, new LinkedList<String>(icons.getCyfaStickerIconIds().keySet()), new LinkedList<Integer>(icons.getCyfaStickerIconIds().values()));
        pages.add(new KeyboardSinglePageView(context, adapter).getView());
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
         return new Sticker_EmojiIcons(mContext, withExpression, false);
    }

    private CharSequence setTitlePage(){
        return "";
    }

}
