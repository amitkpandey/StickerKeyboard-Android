package keyboard.android.psyphertxt.com.stickers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.ads.MobileAds;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import keyboard.android.psyphertxt.com.R;

public class StickerActivity extends AppCompatActivity {

    @Bind(R.id.sticker_grid_list)
    RecyclerView stickerGridView;

    private List<Sticker> stickers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = this.getSharedPreferences(
                "sticker_app", Context.MODE_PRIVATE);
        if(!prefs.getBoolean("FIRST_LAUNCH", false)){
            startActivity(new Intent(StickerActivity.this, StickerFirstTimeActivity.class));
            finish();
        }
        setContentView(R.layout.activity_sticker);

        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_info);
        //upArrow.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        MobileAds.initialize(getApplicationContext(), "pub-1112176598912130");

        ButterKnife.bind(this);

        setupAdapter();
    }

    private void setupAdapter() {

        //initStickers static themes
        stickers = Sticker.initStickers(this);

        if (stickerGridView.getAdapter() == null) {
            StickerAdapter adapter = new StickerAdapter(this, stickers);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            stickerGridView.setLayoutManager(mLayoutManager);
            stickerGridView.setItemAnimator(new DefaultItemAnimator());
            stickerGridView.setAdapter(adapter);
        } else {
            ((StickerAdapter) stickerGridView.getAdapter()).refill(stickers);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add:
                Intent intent=new Intent(android.provider.Settings.ACTION_INPUT_METHOD_SETTINGS);
                startActivity(intent);
                return true;
            case android.R.id.home:
                startActivity(new Intent(StickerActivity.this, StickerAboutActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
