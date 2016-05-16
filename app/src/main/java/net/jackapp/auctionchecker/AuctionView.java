package net.jackapp.auctionchecker;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

    TextView buyItNowTv, priceTv, buyItNowLblTv, priceLblTv, endTimeTv,
            titleTv;
    ImageView pictureIv, pictureIvBg, activeIv;
    ListView historyList;
    LinearLayout historyListLayout, historyRowLayout;
    Auction auction;
    String historyString;
    JSONArray historyArray;
    JSONObject historyJsonObj;
    Intent context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getIntent();

        setContentView(R.layout.auction_view);
        auction = getIntent().getParcelableExtra("auctionToView");
        historyString = getIntent().getStringExtra("history");
        System.out.println("historyString = " + historyString);
        try {
            historyArray = new JSONArray(historyString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        initViewObj();
        setDataViewObj();


    }

    private void initViewObj() {
        buyItNowLblTv = (TextView) findViewById(R.id.buy_it_now_view_lbl);
        buyItNowTv = (TextView) findViewById(R.id.buy_it_now_view);
        priceTv = (TextView) findViewById(R.id.price_view);
        priceLblTv = (TextView) findViewById(R.id.price_view_lbl);
//        urlTv = (TextView) findViewById(R.id.url_view);
        endTimeTv = (TextView) findViewById(R.id.end_time_view);
        activeIv = (ImageView) findViewById(R.id.active_view);
//        countryTv = (TextView) findViewById(R.id.country_view);
        pictureIv = (ImageView) findViewById(R.id.picture_view);
//        pictureIvBg = (ImageView) findViewById(R.id.picture_view_bg);
        titleTv = (TextView) findViewById(R.id.title_view);
        historyList = (ListView) findViewById(R.id.history_list_view);
        historyListLayout = (LinearLayout) findViewById(R.id.history_list_view_layout);
        historyRowLayout = (LinearLayout) findViewById(R.id.history_row_layout);

    }

    private void setDataViewObj() {

        buyItNowTv.setText(auction.getCurrencyBuyItNow());
        priceTv.setText(auction.getCurrencyPrice());
        endTimeTv.setText(auction.getEndDateTime());
//        countryTv.setText(auction.getCountry());
        titleTv.setText(auction.getTitle());

        if (buyItNowTv.getText().equals("-")) {
            buyItNowLblTv.setVisibility(View.GONE);
            buyItNowTv.setVisibility(View.GONE);
            priceLblTv.setText("Buy It Now:");
        } else {
            buyItNowTv.setVisibility(View.VISIBLE);
            buyItNowLblTv.setVisibility(View.VISIBLE);
            priceLblTv.setText("Price:");
        }

        if (auction.getStatus().equals("Active")) {
            activeIv.setImageResource(R.drawable.green_dot);
        } else {
            activeIv.setImageResource(R.drawable.red_dot);
        }

//        urlTv.setText(auction.getUrl());
//        urlTv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent auctionSiteIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(auction.getUrl()));
//                startActivity(auctionSiteIntent);
//            }
//        });

        HistoryAdapter historyAdapter = new HistoryAdapter(getApplicationContext(), historyArray, auction.getCurrency());
        historyList.setAdapter(historyAdapter);
        int historyCount = historyAdapter.getCount();
        int historyHeight = 100;
        for (int i = 0; i < historyAdapter.getCount(); i++) {
            View historyChild = historyAdapter.getView(i, null, historyList);
            historyChild.measure(0,0);
            historyHeight += historyChild.getMeasuredHeight();
        }

        ViewGroup.LayoutParams listParams = historyListLayout.getLayoutParams();
        listParams.height = historyHeight;
        historyListLayout.setLayoutParams(listParams);
        historyListLayout.requestLayout();
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
//                assert pictureIvBg != null;
//                pictureIvBg.setImageBitmap(pic);

                assert pictureIv != null;
                pictureIv.setImageBitmap(pic);

            } else {
                pictureIv.setImageResource(R.drawable.no_img);
//                pictureIvBg.setImageResource(R.drawable.no_img);
            }
        }
    }

}
