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
        ImageView pictureIv = (ImageView) convertView.findViewById(R.id.picture_row);

        try {
            URL picUrl = new URL(auction.getPicture());
            Log.d("jkurl", picUrl.toString());
            InputStream in = picUrl.openConnection().getInputStream();
            picBmp = BitmapFactory.decodeStream(in);
            pictureIv.setImageBitmap(picBmp);
        } catch (IOException e) {
            Log.e("jkE", e.toString());
            e.printStackTrace();
        }

        titleTv.setText(auction.getTitle());
        priceTv.setText(auction.getPrice());

        return convertView;
    }

}
