package net.jackapp.auctionchecker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by jacekkupczak on 09.04.16.
 */

public class AuctionAdapter extends ArrayAdapter<Auction> {

    Bitmap picBmp;

    public AuctionAdapter(Context context, ArrayList<Auction> auctions) {
        super(context, 0, auctions);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Auction auction = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.auction_item, parent, false);
        }

        CaviarTV titleTv = (CaviarTV) convertView.findViewById(R.id.title_row);
        CaviarTV priceTv = (CaviarTV) convertView.findViewById(R.id.price_row);
        CaviarTV bidTv = (CaviarTV) convertView.findViewById(R.id.bid_row);
        ImageView pictureIv = (ImageView) convertView.findViewById(R.id.picture_row);
        ImageView activeIv = (ImageView) convertView.findViewById(R.id.active_row);
        CaviarTV endTimeTv = (CaviarTV) convertView.findViewById(R.id.date_row);
        bidTv.setVisibility(View.VISIBLE);

        try {
            if (auction.getPicture() != "") {
                URL picUrl = new URL(auction.getPicture());
                InputStream in = picUrl.openConnection().getInputStream();
                picBmp = BitmapFactory.decodeStream(in);
                pictureIv.setImageBitmap(picBmp);
            }else {
                pictureIv.setImageResource(R.drawable.img_no_img);
            }
        } catch (IOException e) {
            Log.e("AuctionAdapter getView", e.toString());
            e.printStackTrace();
        }

        titleTv.setText(auction.getTitle());
        endTimeTv.setText(auction.getEndDateTime());

        if (auction.getStatus().equals("Active")) {
            activeIv.setImageResource(R.drawable.green_dot);
        } else {
            activeIv.setImageResource(R.drawable.red_dot);
        }

        if(auction.getCurrencyPrice().equals("-")){
            bidTv.setVisibility(View.GONE);
            priceTv.setText(auction.getCurrencyBid());
        }else {
            priceTv.setText(auction.getCurrencyPrice());
            bidTv.setText(auction.getCurrencyBid());
        }

        notifyDataSetChanged();

        return convertView;
    }

}
