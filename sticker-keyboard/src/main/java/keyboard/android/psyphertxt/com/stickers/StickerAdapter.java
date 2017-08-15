package keyboard.android.psyphertxt.com.stickers;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.ads.Ad;
import com.facebook.ads.AdChoicesView;
import com.facebook.ads.AdError;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.google.android.exoplayer2.C;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.NativeExpressAdView;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

import keyboard.android.psyphertxt.com.R;
import keyboard.android.psyphertxt.com.utilities.ExpandableGridView;

class StickerAdapter extends RecyclerView.Adapter<StickerAdapter.ViewHolder> {

    private Context context;
    private List<Sticker> stickers;
    private int GRIDS_PER_SECTION = 9;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ExpandableGridView stickerGridView;

        public ViewHolder(View view) {
            super(view);
            stickerGridView = (ExpandableGridView) view.findViewById(R.id.sticker_grid);
        }
    }

    StickerAdapter(Context context, List<Sticker> stickers) {
        this.context = context;
        this.stickers = stickers;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sticker_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        List<List<Sticker>> stickerLists = Lists.partition(stickers, GRIDS_PER_SECTION);
        holder.stickerGridView.setExpanded(true);
        setupAdapter(context, stickerLists.get(position), holder.stickerGridView);
    }

    @Override
    public int getItemCount() {
        int count = 0;
        if (stickers != null){
            if(!stickers.isEmpty()){
                count = stickers.size() / GRIDS_PER_SECTION;
                if(stickers.size() % GRIDS_PER_SECTION != 0){
                    count++;
                }
            }
        }
        return count;
    }

    void refill(List<Sticker> stickers) {
        this.stickers.clear();
        this.stickers.addAll(stickers);
        notifyDataSetChanged();

    }


    private void setupAdapter(Context context, List stickers, GridView stickerGridView) {

        if (stickerGridView.getAdapter() == null) {
            StickerGridAdapter adapter = new StickerGridAdapter(context, stickers);
            stickerGridView.setAdapter(adapter);
        } else {
            ((StickerGridAdapter) stickerGridView.getAdapter()).refill(stickers);
        }
    }


}
