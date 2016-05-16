package net.jackapp.auctionchecker;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import org.json.JSONArray;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by jacekkupczak on 01.05.16.
 */
public class AuctionRecyclerAdapter extends RecyclerView.Adapter<AuctionRecyclerAdapter.ViewHolder> {


    private String[] mDataset;
    private ArrayList<Auction> auctions;
    private JSONArray jsonArray;
    private boolean isSmall = true;

    private Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private Button urlBtn, historyBtn;
        private CaviarTV titleTv, endTimeTv, urlTv;
        private CaviarTV buyItNowTv, priceTv;
        private ImageView pictureIv, activeIv, deleteIv;
        private Bitmap picBmp;
        private CardView cardView;

        // each data item is just a string in this case
        public ViewHolder(View row_view) {
            super(row_view);
            titleTv = (CaviarTV) row_view.findViewById(R.id.title_row);
            buyItNowTv = (CaviarTV) row_view.findViewById(R.id.buy_it_now_row);
            priceTv = (CaviarTV) row_view.findViewById(R.id.price_row);
            urlTv = (CaviarTV) row_view.findViewById(R.id.url_row);
            urlBtn = (Button) row_view.findViewById(R.id.url_btn_row);
            historyBtn = (Button) row_view.findViewById(R.id.history_btn_row);
            endTimeTv = (CaviarTV) row_view.findViewById(R.id.date_row);
            pictureIv = (ImageView) row_view.findViewById(R.id.picture_row);
            activeIv = (ImageView) row_view.findViewById(R.id.active_row);
            cardView = (CardView) row_view.findViewById(R.id.card_view_row);
            deleteIv = (ImageView) row_view.findViewById(R.id.delete_row);
            priceTv.setVisibility(View.VISIBLE);
        }

    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public AuctionRecyclerAdapter(Context context, ArrayList<Auction> auctions) {
        this.context = context;
        this.auctions = auctions;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public AuctionRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.auction_item, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final Auction auction = auctions.get(position);

        holder.titleTv.setText(auction.getTitle());
        holder.endTimeTv.setText(auction.getEndDateTime());
        holder.urlTv.setText(auction.getUrl());

        //Animation after click item on main listView
        holder.pictureIv.setOnClickListener(new View.OnClickListener() {
            int lhHigh = 168;
            int lhLow = 95;
            int imgHigh = lhHigh - 10;
            int imgLow = lhLow - 10;
            float alpha = 1;
            int lhDp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, lhHigh, context.getResources().getDisplayMetrics());
            int imgWHDp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, imgHigh, context.getResources().getDisplayMetrics());


            @Override
            public void onClick(final View v) {

                if(holder.cardView.getMeasuredHeight() == (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, lhHigh, context.getResources().getDisplayMetrics())){
                    lhDp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, lhLow, context.getResources().getDisplayMetrics());
                    imgWHDp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, imgLow, context.getResources().getDisplayMetrics());
                    alpha = 0;
                    holder.titleTv.setLines(1);
                }else {
                    lhDp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, lhHigh, context.getResources().getDisplayMetrics());
                    imgWHDp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, imgHigh, context.getResources().getDisplayMetrics());
                    alpha = 1;
                    holder.titleTv.setLines(2);
                }
                final ValueAnimator anim = ValueAnimator.ofInt(v.getMeasuredHeight(), lhDp);
                final ValueAnimator anim2 = ValueAnimator.ofInt(holder.pictureIv.getMeasuredHeight(), imgWHDp);
                final ValueAnimator anim3 = ValueAnimator.ofFloat(holder.historyBtn.getAlpha(), alpha);
                anim3.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float val = (Float) animation.getAnimatedValue();
                        holder.historyBtn.setAlpha(val);
                        holder.urlBtn.setAlpha(val);
                    }
                });
                anim3.setDuration(150);
                anim3.start();
                anim2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        int val = (Integer) animation.getAnimatedValue();
                        ViewGroup.LayoutParams lp = holder.pictureIv.getLayoutParams();
                        lp.width = lp.height = val;
                        holder.pictureIv.setLayoutParams(lp);
                    }
                });
                anim2.start();
                anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        int val = (Integer) animation.getAnimatedValue();
                        ViewGroup.LayoutParams lp = holder.cardView.getLayoutParams();
                        lp.height = val;
                        holder.cardView.setLayoutParams(lp);
                    }
                });
                anim.start();
            }
        });

        //Get www in browser
        holder.urlBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent auctionSiteIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(auctions.get(position).getUrl()));
                context.startActivity(auctionSiteIntent);
            }
        });

        //Get item view
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent showItemIntent = new Intent(context, AuctionView.class);
                Auction auctionToView = auctions.get(position);
                showItemIntent.putExtra("auctionToView", auctionToView);
                showItemIntent.putExtra("history", auctionToView.getHistoryString());
                context.startActivity(showItemIntent);
                System.out.println("v.toString() = " + auctionToView.getTitle());
            }
        });

        holder.deleteIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuctionWorker.removeAuction(context, auction.getItemId());
                auctions.remove(position);
                notifyDataSetChanged();
                System.out.println("Auctions afrer delete ");
                StaticWorker.showDBTitles();
            }
        });

        try {
            if (auction.getPicture() != "") {
                URL picUrl = new URL(auction.getPicture());
                InputStream in = picUrl.openConnection().getInputStream();
                BitmapFactory.Options bfOptions = new BitmapFactory.Options();
                bfOptions.inJustDecodeBounds = false;
                bfOptions.inSampleSize = 3;
                holder.picBmp = BitmapFactory.decodeStream(in, null, bfOptions);
                holder.pictureIv.setImageBitmap(holder.picBmp);
            } else {
                holder.pictureIv.setImageResource(R.drawable.img_no_img);
            }
        } catch (IOException e) {
            Log.e("AuctionAdapter getView", e.toString());
            e.printStackTrace();
        }

        if (auction.getStatus().equals("Active")) {
            holder.activeIv.setImageResource(R.drawable.green_dot);
        } else {
            holder.activeIv.setImageResource(R.drawable.red_dot);
        }

        if (auction.getCurrencyBuyItNow().equals("-")) {
            holder.priceTv.setVisibility(View.GONE);
            holder.buyItNowTv.setText(auction.getCurrencyPrice());
        } else {
            holder.buyItNowTv.setText(auction.getCurrencyBuyItNow());
            holder.priceTv.setText(auction.getCurrencyPrice());
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return auctions.size();
    }


}
