package net.jackapp.auctionchecker;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Created by jacekkupczak on 10.04.16.
 */
public class AuctionView extends Activity {

    TextView price, bid, url, endTime;
    Auction auction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.auction_view);
        auction = getIntent().getParcelableExtra("auction");

        initViewObj();
        setDataViewObj();


    }

    private void initViewObj(){
        price = (TextView) findViewById(R.id.price_view);
        bid = (TextView) findViewById(R.id.bid_view);
        url = (TextView) findViewById(R.id.url_view);
        endTime = (TextView) findViewById(R.id.end_time_view);
    }

    private void setDataViewObj(){
        price.setText(auction.getPrice());
        bid.setText(auction.getBid());
        url.setText(auction.getUrl());
        endTime.setText(auction.getEndTime());
    }
}
