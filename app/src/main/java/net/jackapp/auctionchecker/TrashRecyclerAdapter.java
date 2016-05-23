package net.jackapp.auctionchecker;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by jacekkupczak on 01.05.16.
 */
public class TrashRecyclerAdapter extends RecyclerView.Adapter<TrashRecyclerAdapter.ViewHolder> {


    private ArrayList<Auction> auctions;
    private ViewHolder viewHolder;
    private TextView infoTrashTv;
    private Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView titleTv, priceTv;
        private ImageView pictureIv;
        private LinearLayout restore;
        private ProgressBar progressBar;
        private CardView cardView;

        public ViewHolder(View row_view) {
            super(row_view);
            titleTv = (TextView) row_view.findViewById(R.id.title_row);
            priceTv = (TextView) row_view.findViewById(R.id.price_row);
            pictureIv = (ImageView) row_view.findViewById(R.id.picture_row);
            cardView = (CardView) row_view.findViewById(R.id.card_view_row);
            restore = (LinearLayout) row_view.findViewById(R.id.restore_layout_trash);
            progressBar = (ProgressBar) row_view.findViewById(R.id.progress_bar_row);
            priceTv.setVisibility(View.VISIBLE);
        }

    }

    public TrashRecyclerAdapter(Context context, ArrayList<Auction> auctions, TextView infoTrashTv) {
        this.context = context;
        this.auctions = auctions;
        this.infoTrashTv = infoTrashTv;
    }

    @Override
    public TrashRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.trash_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final Auction auction = auctions.get(position);
        this.viewHolder = holder;
        holder.titleTv.setText(auction.getTitle());
        holder.priceTv.setText(auction.getCurrencyPrice());

        //Get item view
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent showItemIntent = new Intent(context, AuctionView.class);
                Auction auctionToView = auctions.get(position);
                System.out.println("auction.getTrash() = " + auction.getTrash());
                showItemIntent.putExtra("trash", auction.getTrash());
                showItemIntent.putExtra("auctionToView", auctionToView);
                showItemIntent.putExtra("history", auctionToView.getHistoryString());
                context.startActivity(showItemIntent);
            }
        });

        holder.restore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuctionWorker.setTrash(context, auction.getItemId(), false);
                auctions.remove(position);
                notifyDataSetChanged();
                StaticWorker.showDBTitles();
                if(auctions.isEmpty()) infoTrashTv.setVisibility(View.VISIBLE);
            }
        });

        if (auction.getPicture() != "") {
            ImageLoader imageLoader = new ImageLoader(context);
            imageLoader.displayImage(auction.getPicture(), holder.pictureIv, holder.progressBar);
        } else {
            holder.pictureIv.setImageResource(R.drawable.img_no_img);
        }

    }

    @Override
    public int getItemCount() {
        return auctions.size();
    }


}
