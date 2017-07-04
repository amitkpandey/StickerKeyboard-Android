package keyboard.android.psyphertxt.com.gkeyboard.stickers;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.NativeExpressAdView;
import com.google.common.collect.Lists;

import java.util.List;

import keyboard.android.psyphertxt.com.gkeyboard.R;
import keyboard.android.psyphertxt.com.gkeyboard.utilities.ExpandableGridView;

class StickerAdapter extends RecyclerView.Adapter<StickerAdapter.ViewHolder> {

    private Context context;
    private List<Sticker> stickers;
    private int GRIDS_PER_SECTION = 9;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ExpandableGridView stickerGridView;
        public LinearLayout admobFrame;

        public ViewHolder(View view) {
            super(view);
            stickerGridView = (ExpandableGridView) view.findViewById(R.id.sticker_grid);
            admobFrame = (LinearLayout) view.findViewById(R.id.admob_frame);
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
        addAdMobView(holder);
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

    private void addAdMobView(ViewHolder holder){
        ViewGroup adCardView = holder.admobFrame;
        AdRequest adRequest = new AdRequest.Builder()
//                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
                .addTestDevice("CCDF3FFB9F1C5F61511338E52C46D7E3")  // My Galaxy Nexus test phone
                .build();
        View view = View.inflate(holder.admobFrame.getContext(), R.layout.admob_sticker_item, adCardView);
        final NativeExpressAdView mAdView = (NativeExpressAdView) view.findViewById(R.id.adView);
        mAdView.loadAd(adRequest);
        mAdView.setVisibility(View.GONE);
        mAdView.setAdListener(new AdListener(){
            @Override
            public void onAdLoaded() {
                mAdView.setVisibility(View.VISIBLE);
            }
        });

        // The NativeExpressAdViewHolder recycled by the RecyclerView may be a different
        // instance than the one used previously for this position. Clear the
        // NativeExpressAdViewHolder of any subviews in case it has a different
        // AdView associated with it, and make sure the AdView for this position doesn't
        // already have a parent of a different recycled NativeExpressAdViewHolder.
        if (adCardView.getChildCount() > 0) {
            adCardView.removeAllViews();
        }
        if (mAdView.getParent() != null) {
            ((ViewGroup) mAdView.getParent()).removeView(mAdView);
        }

        // Add the Native Express ad to the native express ad view.
        adCardView.addView(mAdView);
    }
}
