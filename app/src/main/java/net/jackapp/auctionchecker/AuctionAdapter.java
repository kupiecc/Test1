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
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by jacekkupczak on 09.04.16.
 */

public class AuctionAdapter extends ArrayAdapter<Auction> {

    Bitmap picBmp;

    public AuctionAdapter(Context context, ArrayList<Auction> auctions) {
        super(context, 0, auctions);
    }

    public void getTitle(){
        Log.d("jk", "getTitle()");
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

        try {
            URL picUrl = new URL(auction.getPicture());
            InputStream in = picUrl.openConnection().getInputStream();
            picBmp = BitmapFactory.decodeStream(in);
            pictureIv.setImageBitmap(picBmp);
        } catch (IOException e) {
            Log.e("jkE", e.toString());
            e.printStackTrace();
        }

        titleTv.setText(auction.getTitle());

        DecimalFormat df = new DecimalFormat("###,###.00");
        if (!auction.getPrice().equals("-")) {
            String priceFormat = df.format(Double.valueOf(auction.getPrice())) + " " + auction.getCurrency();
            priceTv.setText(priceFormat);
        }
        else priceTv.setText("-");

        if (!auction.getBid().equals("-")) {
            String bidFormat = df.format(Double.valueOf(auction.getBid())) + " " + auction.getCurrency();
            bidTv.setText(bidFormat);
        }
        else bidTv.setText("");

        notifyDataSetChanged();

        return convertView;
    }

}
