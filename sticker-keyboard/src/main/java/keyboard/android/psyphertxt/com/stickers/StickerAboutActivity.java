package keyboard.android.psyphertxt.com.stickers;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;

import keyboard.android.psyphertxt.com.R;

public class StickerAboutActivity extends AppCompatActivity {

    ImageView instagramIv, twitterIv, facebookIv;
    TextView privacyPolicyTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sticker_about);

        instagramIv = (ImageView) findViewById(R.id.iv_instagram);
        twitterIv = (ImageView) findViewById(R.id.iv_twitter);
        facebookIv = (ImageView) findViewById(R.id.iv_facebook);
        privacyPolicyTv = (TextView) findViewById(R.id.tv_privacy_policy);

        instagramIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Answers.getInstance().logCustom(new CustomEvent("About Us ClickEvent")
                        .putCustomAttribute("Name", "Instagram"));
                openWebIntent("https://www.instagram.com/gstickerss/");
            }
        });

        twitterIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Answers.getInstance().logCustom(new CustomEvent("About Us ClickEvent")
                        .putCustomAttribute("Name", "Twitter"));
                openWebIntent("https://twitter.com/gstickerss");
            }
        });

        facebookIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Answers.getInstance().logCustom(new CustomEvent("About Us ClickEvent")
                        .putCustomAttribute("Name", "Facebook"));
                openWebIntent("https://www.facebook.com/gstickerss/");
            }
        });


        privacyPolicyTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Answers.getInstance().logCustom(new CustomEvent("About Us ClickEvent")
                        .putCustomAttribute("Name", "Privacy Policy"));
                openWebIntent("http://cyfa.io/privacy-policy/gstickers/sticker-privacy-policy.html");
            }
        });
    }

    private void openWebIntent(String string){
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(string));
        startActivity(i);
    }
}
