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
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by jacekkupczak on 10.04.16.
 */
public class AuctionView extends Activity {

    TextView priceTv, bidTv, urlTv, endTimeTv, activeTv, countryTv;
    ImageView pictureIv, pictureIvBg;
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
        priceTv = (TextView) findViewById(R.id.price_view);
        bidTv = (TextView) findViewById(R.id.bid_view);
        urlTv = (TextView) findViewById(R.id.url_view);
        endTimeTv = (TextView) findViewById(R.id.end_time_view);
        activeTv = (TextView) findViewById(R.id.active_view);
        countryTv = (TextView) findViewById(R.id.country_view);
        pictureIv = (ImageView) findViewById(R.id.picture_view);
        pictureIvBg = (ImageView) findViewById(R.id.picture_view_bg);

    }

    private void setDataViewObj(){

        priceTv.setText(auction.getCurrencyPrice());
        bidTv.setText(auction.getCurrencyBid());
        endTimeTv.setText(auction.getEndDateTime());
        activeTv.setText(auction.getStatus());
        countryTv.setText(auction.getCountry());

        urlTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent auctionSiteIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(auction.getUrl()));
                startActivity(auctionSiteIntent);
            }
        });

        new GetAuctionPic().execute(auction.getPicture());

    }


    class GetAuctionPic extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap pic = null;
            try {
                pic = BitmapFactory.decodeStream((InputStream) new URL(params[0]).getContent());
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

            }
        }
    }

}
