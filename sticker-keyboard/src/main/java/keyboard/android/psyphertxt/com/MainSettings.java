package keyboard.android.psyphertxt.com;

import android.os.Bundle;
import android.preference.PreferenceActivity;


public class MainSettings extends PreferenceActivity {

    public static String CHANGE_ICON_SET_KEY =  "";
    public static String CHANGE_ICON_SET_VALUE_GOOGLE = "";
    public static String CHANGE_ICON_SET_VALUE_DEFAULT = "";
    public static String CHANGE_ICON_SET_VALUE_APPLE = "";


    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        CHANGE_ICON_SET_KEY = getResources().getString(R.string.setting_change_icon_set_key);
        CHANGE_ICON_SET_VALUE_GOOGLE = getResources().getString(R.string.setting_change_icon_set_value_google);
        CHANGE_ICON_SET_VALUE_DEFAULT = CHANGE_ICON_SET_VALUE_GOOGLE;
        CHANGE_ICON_SET_VALUE_APPLE = getResources().getString(R.string.setting_change_icon_set_value_apple);
    }
}
