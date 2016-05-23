package net.jackapp.auctionchecker;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by jacekkupczak on 10.04.16.
 */
public class AuctionView extends AppCompatActivity {

    Auction auction;
    ImageView pictureIv, activeIv, urlIv, trashIv;
    Intent context;
    JSONArray historyArray;
    ListView historyList;
    LinearLayout historyListLayout, historyRowLayout;
    ProgressBar progressBar;
    String historyString;
    boolean inTrash;
    TextView buyItNowTv, priceTv, buyItNowLblTv, priceLblTv, endTimeTv, titleTv, trashInfoTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getIntent();

        setContentView(R.layout.auction_view);
        auction = getIntent().getParcelableExtra("auctionToView");
        historyString = getIntent().getStringExtra("history");
        inTrash = getIntent().getBooleanExtra("trash", false);
        Toolbar toolbar = (Toolbar) findViewById(R.id.view_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
        urlIv = (ImageView) findViewById(R.id.url_view);
        endTimeTv = (TextView) findViewById(R.id.end_time_view);
        activeIv = (ImageView) findViewById(R.id.active_view);
        pictureIv = (ImageView) findViewById(R.id.picture_view);
        trashIv = (ImageView) findViewById(R.id.trash_view);
        titleTv = (TextView) findViewById(R.id.title_view);
        trashInfoTv = (TextView) findViewById(R.id.trash_info_view);
        historyList = (ListView) findViewById(R.id.history_list_view);
        historyListLayout = (LinearLayout) findViewById(R.id.history_list_view_layout);
        historyRowLayout = (LinearLayout) findViewById(R.id.history_row_layout);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar_view);

    }

    private void setDataViewObj() {

        buyItNowTv.setText(auction.getCurrencyBuyItNow());
        priceTv.setText(auction.getCurrencyPrice());
        endTimeTv.setText(auction.getEndDateTime());
        titleTv.setText(auction.getTitle());

        if(inTrash){
            trashInfoTv.setVisibility(View.VISIBLE);
            trashIv.setImageResource(R.mipmap.ic_restore_white_24dp);
        }else {
            trashInfoTv.setVisibility(View.GONE);
            trashIv.setImageResource(R.mipmap.ic_delete_white_24dp);
        }

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

        urlIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent auctionSiteIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(auction.getUrl()));
                startActivity(auctionSiteIntent);
            }
        });
        trashIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auction.setTrash(!inTrash);
                AuctionWorker.setTrash(getApplicationContext(), auction.getItemId(), !inTrash);
                inTrash = !inTrash;
                setDataViewObj();
            }
        });

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
        ImageLoader imageLoader = new ImageLoader(getApplicationContext());
        imageLoader.displayImage(auction.getPicture(), pictureIv, progressBar);

    }

}
