package net.jackapp.auctionchecker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by jacekkupczak on 09.04.16.
 */

public class AuctionAdapter extends ArrayAdapter<Auction> {

    public AuctionAdapter(Context context, ArrayList<Auction> auctions) {
        super(context, 0, auctions);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        Auction auction = getItem(position);

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.auction_item, parent, false);
        }

        CaviarTV titleTv = (CaviarTV) convertView.findViewById(R.id.title_row);
        CaviarTV priceTv = (CaviarTV) convertView.findViewById(R.id.price_row);
        ImageView pictureIv = (ImageView) convertView.findViewById(R.id.picture_row);

//        new MainActivity.GetAuctionPic().execute(auction.getPicture());

        titleTv.setText(auction.getTitle());
        priceTv.setText(auction.getPrice());

        return convertView;
    }

}
