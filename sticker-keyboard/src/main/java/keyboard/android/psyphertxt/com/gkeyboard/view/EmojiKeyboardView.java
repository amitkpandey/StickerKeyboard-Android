package keyboard.android.psyphertxt.com.gkeyboard.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import keyboard.android.psyphertxt.com.gkeyboard.EmojiKeyboardService;
import keyboard.android.psyphertxt.com.gkeyboard.adapter.EmojiPagerAdapter;
import com.klinker.android.emoji_keyboard_trial.R;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class EmojiKeyboardView extends View implements SharedPreferences.OnSharedPreferenceChangeListener,SmartTabLayout.TabProvider{

    private ViewPager viewPager;
    private SmartTabLayout pagerSlidingTabStrip;
    private LinearLayout layout;

    private EmojiPagerAdapter emojiPagerAdapter;
    private EmojiKeyboardService emojiKeyboardService;

    public EmojiKeyboardView(Context context) {
        super(context);
        initialize(context);
    }

    public EmojiKeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public EmojiKeyboardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
    }

    private void initialize(Context context) {

        emojiKeyboardService = (EmojiKeyboardService) context;

        LayoutInflater inflater = (LayoutInflater)   getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        layout = (LinearLayout) inflater.inflate(R.layout.keyboard_main, null);

        viewPager = (ViewPager) layout.findViewById(R.id.emojiKeyboard);

        pagerSlidingTabStrip = (SmartTabLayout) layout.findViewById(R.id.emojiCategorytab);

        pagerSlidingTabStrip.setCustomTabView(this);
        //pagerSlidingTabStrip.setSelectedTabIndicatorColor(getResources().getColor(R.color.holo_blue));

        //pagerSlidingTabStrip.setSelectedTabIndicatorHeight(6);

        emojiPagerAdapter = new EmojiPagerAdapter(context, viewPager, height);

        viewPager.setAdapter(emojiPagerAdapter);

        setupDeleteButton();

        pagerSlidingTabStrip.setViewPager(viewPager);

        viewPager.setCurrentItem(1);

        PreferenceManager.getDefaultSharedPreferences(context).registerOnSharedPreferenceChangeListener(this);
    }

    public View getView() {
        return layout;
    }

    public void notifyDataSetChanged() {
        emojiPagerAdapter.notifyDataSetChanged();
        viewPager.refreshDrawableState();
    }

    private void setupDeleteButton() {

        ImageView delete = (ImageView) layout.findViewById(R.id.deleteButton);

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imeManager = (InputMethodManager) v.getContext().getApplicationContext().getSystemService(INPUT_METHOD_SERVICE);
                imeManager.showInputMethodPicker();
            }
        });

        delete.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                emojiKeyboardService.switchToPreviousInputMethod();
                return false;
            }
        });
    }


    private int width;
    private int height;
    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        width = View.MeasureSpec.getSize(widthMeasureSpec);
        height = View.MeasureSpec.getSize(heightMeasureSpec);

        Log.d("emojiKeyboardView", width +" : " + height);
        setMeasuredDimension(width, height);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        Log.d("sharedPreferenceChange", "function called on change of shared preferences with key " + key);
        if (key.equals("icon_set")){
            emojiPagerAdapter = new EmojiPagerAdapter(getContext(), viewPager, height);
            viewPager.setAdapter(emojiPagerAdapter);
            this.invalidate();
        }
    }

    @Override
    public View createTabView(ViewGroup container, int position, PagerAdapter adapter) {
        Resources res = container.getContext().getResources();
        ImageView icon = new ImageView(container.getContext());
        LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity= Gravity.CENTER;
        layoutParams.setMargins(10,0,10,0);
        icon.setLayoutParams(layoutParams);
        icon.setPadding(20,0,20,0);
        icon.setColorFilter(Color.parseColor("#555555"));
        switch (position) {
            case 0:
                icon.setImageDrawable(res.getDrawable(R.drawable.ic_history_black_24dp));
                break;
            case 1:
                icon.setImageDrawable(res.getDrawable(R.drawable.ic_sentiment_neutral_black_24dp));
                break;
            default:
                throw new IllegalStateException("Invalid position: " + position);
        }
        return icon;
    }
}
