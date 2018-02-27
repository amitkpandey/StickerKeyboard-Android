package keyboard.android.psyphertxt.com.stickers;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;

import keyboard.android.psyphertxt.com.BuildConfig;
import keyboard.android.psyphertxt.com.R;

public class StickerAboutActivity extends AppCompatActivity {

    private Uri mInvitationUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sticker_about);

        ImageView instagramIv = findViewById(R.id.iv_instagram);
        ImageView twitterIv =  findViewById(R.id.iv_twitter);
        ImageView facebookIv =  findViewById(R.id.iv_facebook);
        TextView privacyPolicyTv =  findViewById(R.id.tv_privacy_policy);

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


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {
            String uid = user.getUid();
            String link = "https://cyfa.io/?invitedby=" + uid;
            FirebaseDynamicLinks.getInstance().createDynamicLink()
                    .setLink(Uri.parse(link))
                    .setDynamicLinkDomain("h3yet.app.goo.gl")
                    .setAndroidParameters(
                            new DynamicLink.AndroidParameters.Builder(BuildConfig.APPLICATION_ID)
                                    .setMinimumVersion(28)
                                    .build())
                    .setIosParameters(
                            new DynamicLink.IosParameters.Builder("com.psyphertxt.stickers.app.GhanaTok")
                                    .setAppStoreId("1269549615")
                                    .setMinimumVersion("1.2")
                                    .build())
                    .buildShortDynamicLink()
                    .addOnSuccessListener(new OnSuccessListener<ShortDynamicLink>() {
                        @Override
                        public void onSuccess(ShortDynamicLink shortDynamicLink) {
                            mInvitationUrl = shortDynamicLink.getShortLink();
                        }
                    });
        }
    }

    private void openWebIntent(String string){
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(string));
        startActivity(i);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menuabout, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share:
                Answers.getInstance().logCustom(new CustomEvent("About Click Event")
                        .putCustomAttribute("Name", "Share App"));
                if(BuildConfig.APPLICATION_ID.contains("gkeyboard")){
                    shareAppWithFriends("Ghamoji");
                }else if(BuildConfig.APPLICATION_ID.contains("gtokkeyboard")){
                    shareAppWithFriends("GhanaTok");
                }else if(BuildConfig.APPLICATION_ID.contains("foodstickers")){
                    shareAppWithFriends("Food Stickers");
                }else if(BuildConfig.APPLICATION_ID.contains("naijadylog")){
                    shareAppWithFriends("Naija Dylog");
                }else if(BuildConfig.APPLICATION_ID.contains("lovetok")){
                    shareAppWithFriends("LoveTok");
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void shareAppWithFriends(String appname){
        String subject = "Hi friend,  I want you to try G-Stickers, "+ appname+"!";
        String invitationLink = mInvitationUrl == null ? "https://h3yet.app.goo.gl/ZPbE" : mInvitationUrl.toString();
        String msg = "Use everyday expressions stickers and emojis to chat with your friends: "
                + invitationLink;
        String msgHtml = String.format("<p>Let's chat using expressions! Use my "
                + "<a href=\"%s\">referrer link</a>!</p>", invitationLink);

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, subject+"\n"+msg);
//        intent.putExtra(Intent.EXTRA_HTML_TEXT, msgHtml);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}
