package net.jackapp.auctionchecker;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by jacekkupczak on 10.04.16.
 */
public class AuctionView extends Activity {

    TextView priceTv, bidTv, priceLblTv, bidLblTv, urlTv, endTimeTv, countryTv, titleTv;
    ImageView pictureIv, pictureIvBg, activeIv;
    ListView historyList;
    Auction auction;
    String historyString;
    JSONArray historyArray;
    JSONObject historyJsonObj;
    Intent context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getIntent();
        System.out.println("context = " + context);

        setContentView(R.layout.auction_view);
        auction = getIntent().getParcelableExtra("auctionToView");
        historyString = getIntent().getStringExtra("history");
        try {
            historyArray = new JSONArray(historyString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        initViewObj();
        setDataViewObj();


    }

    private void initViewObj() {
        priceLblTv = (TextView) findViewById(R.id.price_view_lbl);
        priceTv = (TextView) findViewById(R.id.price_view);
        bidTv = (TextView) findViewById(R.id.bid_view);
        bidLblTv = (TextView) findViewById(R.id.bid_view_lbl);
        urlTv = (TextView) findViewById(R.id.url_view);
        endTimeTv = (TextView) findViewById(R.id.end_time_view);
        activeIv = (ImageView) findViewById(R.id.active_view);
        countryTv = (TextView) findViewById(R.id.country_view);
        pictureIv = (ImageView) findViewById(R.id.picture_view);
        pictureIvBg = (ImageView) findViewById(R.id.picture_view_bg);
        titleTv = (TextView) findViewById(R.id.title_view);
        historyList = (ListView) findViewById(R.id.history_list_view);

    }

    private void setDataViewObj() {

        priceTv.setText(auction.getCurrencyPrice());
        bidTv.setText(auction.getCurrencyBid());
        endTimeTv.setText(auction.getEndDateTime());
        countryTv.setText(auction.getCountry());
        titleTv.setText(auction.getTitle());

        if (priceTv.getText().equals("-")) {
            priceLblTv.setVisibility(View.GONE);
            priceTv.setVisibility(View.GONE);
            bidLblTv.setText("Price:");
        } else {
            priceTv.setVisibility(View.VISIBLE);
            priceLblTv.setVisibility(View.VISIBLE);
            bidLblTv.setText("Bid:");
        }

        if (auction.getStatus().equals("Active")) {
            activeIv.setImageResource(R.drawable.green_dot);
        } else {
            activeIv.setImageResource(R.drawable.red_dot);
        }

        urlTv.setText(auction.getUrl());
        urlTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent auctionSiteIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(auction.getUrl()));
                startActivity(auctionSiteIntent);
            }
        });

        HistoryAdapter historyAdapter = new HistoryAdapter(getApplicationContext(), historyArray, auction.getCurrency());
        historyList.setAdapter(historyAdapter);
        new GetAuctionPic().execute(auction.getPicture());

    }


    class GetAuctionPic extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap pic = null;
            try {
                if (params[0] != null && !params[0].equals("")) {
                    pic = BitmapFactory.decodeStream((InputStream) new URL(params[0]).getContent());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return pic;
        }

        @Override
        protected void onPostExecute(Bitmap pic) {
            super.onPostExecute(pic);

            if (pic != null) {
                assert pictureIvBg != null;
                pictureIvBg.setImageBitmap(pic);

                assert pictureIv != null;
                pictureIv.setImageBitmap(pic);

            } else {
                pictureIv.setImageResource(R.drawable.no_img);
                pictureIvBg.setImageResource(R.drawable.no_img);
            }
        }
    }

}
