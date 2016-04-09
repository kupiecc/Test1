package net.jackapp.auctionchecker;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity {

//    public static ArrayAdapter<String> auctionAdapter;
    private final String JSON_DB_NAME = "auctionDB.json";
    FileWorker fileWorker = new FileWorker();
    ArrayList<Auction> auctionList = new ArrayList<>();
    String itemId, auctionDBString;
    JSONObject auctionDBJson;
    JSONArray auctionDBArray = new JSONArray();
    Bitmap picBmp;
    Typeface caviar, caviarBold;
    TextView urlTv, priceTv, pasteBtn, titleTv, bidTv;
    ListView listAuctionLv;
    Auction foundAuction;
    final String ITEM = "Item"; final String BID = "ConvertedBuyItNowPrice"; final String PICTURE_URL = "PictureURL"; final String PRICE = "ConvertedCurrentPrice"; final String TITLE = "Title"; final String VALUE = "Value"; final String CURRENCY = "CurrencyID"; final String ITEM_ID = "ItemID"; final String VERSION = "Version"; final String END_TIME = "EndTime"; final String AUCTION_URL = "ViewItemURLForNaturalSearch"; final String LISTING_TYPE = "ListingType"; final String LOCATION = "Location"; final String CATEGORY = "PrimaryCategoryName"; final String CATEGORY_ID = "PrimaryCategoryID"; final String STATUS = "ListingStatus"; final String COUNTRY = "Country"; final String TIMESTAMP = "Timestamp"; final String ACK = "Ack";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        urlTv = (TextView) findViewById(R.id.url_auction);
        priceTv = (TextView) findViewById(R.id.price_found);
        pasteBtn = (TextView) findViewById(R.id.paste_btn);
        titleTv = (TextView) findViewById(R.id.title_found);
        bidTv = (TextView) findViewById(R.id.bid_found);
        listAuctionLv = (ListView) findViewById(R.id.list_auction);

        caviar = Typeface.createFromAsset(getAssets(), "cd.ttf");
        caviarBold = Typeface.createFromAsset(getAssets(), "cdb.ttf");
        urlTv.setTypeface(caviar);
        priceTv.setTypeface(caviar);
        pasteBtn.setTypeface(caviar);
        titleTv.setTypeface(caviar);
        bidTv.setTypeface(caviar);

        try {
            fileWorker.createJsonFile(this, JSON_DB_NAME);
            fileWorker.readJsonFile(this, JSON_DB_NAME);
            auctionDBString = fileWorker.readJsonFile(this, JSON_DB_NAME);
            Log.d("jkDBString", auctionDBString);
            if(!auctionDBString.equals("")) {
                auctionDBArray = new JSONArray(auctionDBString);
            }else{
                auctionDBArray = new JSONArray();
            }
            loadJsonToLv();
        } catch (JSONException e) {
            Log.e("jk Error", e.toString());
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onPasteAuctionLink(View view) {

        urlTv.setTextColor(Color.parseColor("#ffffff"));
        analyzeClipUrl();

    }

    private void analyzeClipUrl() {
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

        if (clipboardManager.hasPrimaryClip()) {
            ClipData.Item clpItem = clipboardManager.getPrimaryClip().getItemAt(0);
            if (clpItem != null) {
                String urlToCheck = clpItem.getText().toString();
                urlTv.setText(urlToCheck);
                itemId = getAuctionId(urlToCheck);
            }
        }


        new GetAuctionJSON().execute();
    }

    private String getAuctionId(String url) {

        Uri urlToParse = Uri.parse(url);
        String protocol = urlToParse.getScheme();
        String server = urlToParse.getAuthority();
        String path = urlToParse.getPath();
        String[] pathArr = path.split("/");

        return pathArr[pathArr.length - 1];

    }

    
    private void loadJsonToLv() throws JSONException {

        ArrayList<Auction> auctionsList = new ArrayList<>();
        Auction auctionFromJson = new Auction();
        try{
            for(int i=0; i < auctionDBArray.length(); i++){
                Log.d("jk", auctionDBArray.getJSONObject(i).getString(TITLE));
                if(auctionDBArray.getJSONObject(i).has(TITLE)) auctionFromJson.setTitle(auctionDBArray.getJSONObject(i).getString(TITLE));
                if(auctionDBArray.getJSONObject(i).has(ITEM_ID)) auctionFromJson.setItemId(auctionDBArray.getJSONObject(i).getString(ITEM_ID));
                if(auctionDBArray.getJSONObject(i).has(END_TIME)) auctionFromJson.setEndTime(auctionDBArray.getJSONObject(i).getString(END_TIME));
                if(auctionDBArray.getJSONObject(i).has(AUCTION_URL)) auctionFromJson.setUrl(auctionDBArray.getJSONObject(i).getString(AUCTION_URL));
                if(auctionDBArray.getJSONObject(i).has(LOCATION)) auctionFromJson.setLocation(auctionDBArray.getJSONObject(i).getString(LOCATION));
                if(auctionDBArray.getJSONObject(i).has(PICTURE_URL)) {
                    auctionFromJson.setPicture(auctionDBArray.getJSONObject(i).getString(PICTURE_URL));
                }
                if(auctionDBArray.getJSONObject(i).has(PRICE)) auctionFromJson.setPrice(auctionDBArray.getJSONObject(i).getString(PRICE));
                if(auctionDBArray.getJSONObject(i).has(BID)) auctionFromJson.setBid(auctionDBArray.getJSONObject(i).getString(BID));
                if(auctionDBArray.getJSONObject(i).has(CURRENCY)) auctionFromJson.setCurrency(auctionDBArray.getJSONObject(i).getString(CURRENCY));
                if(auctionDBArray.getJSONObject(i).has(STATUS)) auctionFromJson.setStatus(auctionDBArray.getJSONObject(i).getString(STATUS));
                if(auctionDBArray.getJSONObject(i).has(COUNTRY)) auctionFromJson.setCountry(auctionDBArray.getJSONObject(i).getString(COUNTRY));
                auctionsList.add(auctionFromJson);
            }
            AuctionAdapter auctionAdapter = new AuctionAdapter(getApplicationContext(), auctionsList);
            listAuctionLv.setAdapter(auctionAdapter);
        }catch (JSONException e){
            Log.e("jkE", e.toString());
        }


    }
    
    public void saveAuction(View view) throws JSONException {

        JSONObject itemJson = new JSONObject();

        try{

            itemJson.put(ITEM_ID, foundAuction.getItemId());
            itemJson.put(END_TIME, foundAuction.getEndTime());
            itemJson.put(AUCTION_URL, foundAuction.getUrl());
            itemJson.put(LOCATION, foundAuction.getLocation());
            itemJson.put(PICTURE_URL, foundAuction.getPicture());
            itemJson.put(CATEGORY_ID, foundAuction.getCategoryId());
            itemJson.put(CATEGORY, foundAuction.getCategory());
            itemJson.put(PRICE, foundAuction.getPrice());
            itemJson.put(BID, foundAuction.getBid());
            itemJson.put(CURRENCY, foundAuction.getCurrency());
            itemJson.put(STATUS, foundAuction.getStatus());
            itemJson.put(TITLE, foundAuction.getTitle());
            itemJson.put(COUNTRY, foundAuction.getCountry());
            auctionDBArray.put(itemJson);

            Log.d("jk", auctionDBArray.toString());

            fileWorker.writeJsonFile(this, auctionDBArray.toString(), JSON_DB_NAME);

        } catch (JSONException e) {
            Log.d("jk", "saveAuction function problem: " + e.toString());
            e.printStackTrace();
        }
    }

    public void clearFile(View view) {
        fileWorker.writeJsonFile(this, "", JSON_DB_NAME);
    }

    class GetAuctionJSON extends AsyncTask<Void, Void, Void> {

        final String EBAY_SHOPPING_URL = "http://open.api.ebay.com/shopping?"; final String APP_ID = "Individu-PriceChe-PRD-53a0284bf-5b5a8f9b"; final String QUERY_PARAM = "callname"; final String FORMAT_PARAM = "responseencoding"; final String APPID_PARAM = "appid"; final String SITEID_PARAM = "siteid"; final String VERSION_PARAM = "version"; final String ITEMID_PARAM = "itemID";

        String jsonString = ""; String auctionPrice = ""; String auctionBid = ""; String auctionCurrencyID = "";
        String dataTypeJson = "JSON"; String siteId = "0"; String query_type = "GetSingleItem"; String version = "515";


        @Override
        protected Void doInBackground(Void... params) {

            try {
                Uri builtUri = Uri.parse(EBAY_SHOPPING_URL).buildUpon().appendQueryParameter(QUERY_PARAM, query_type).appendQueryParameter(FORMAT_PARAM, dataTypeJson).appendQueryParameter(APPID_PARAM, APP_ID).appendQueryParameter(SITEID_PARAM, siteId).appendQueryParameter(VERSION_PARAM, version).appendQueryParameter(ITEMID_PARAM, itemId).build();

                URL urlToCheck = new URL(builtUri.toString()); Log.d("jk", builtUri.toString());

                HttpURLConnection urlConnection = (HttpURLConnection) urlToCheck.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();

                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                jsonString = sb.toString();

                JSONObject jObject = new JSONObject(jsonString);
                getAuctionItem(jsonString);

            } catch (MalformedURLException e) { e.printStackTrace(); } catch (IOException e) { e.printStackTrace(); } catch (JSONException e) { e.printStackTrace(); }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            setFoundAuctionToView(foundAuction);
        }

        private void setFoundAuctionToView(Auction fa) {
            titleTv.setText(fa.getTitle());
            priceTv.setText(fa.getPrice());
            bidTv.setText(fa.getBid());
            AuctionAdapter auctionAdapter = new AuctionAdapter(getApplicationContext(), auctionList);
            listAuctionLv.setAdapter(auctionAdapter);

        }

        protected void getAuctionItem(String jsonString) throws JSONException {

            foundAuction = new Auction();

            JSONObject jsonObject = new JSONObject(jsonString);

            JSONObject jItem = jsonObject.getJSONObject(ITEM);
            Iterator it = jItem.keys();
            JSONObject jPrice;
            JSONArray jPic;

            foundAuction.setVersion(jsonObject.get(VERSION).toString());
            foundAuction.setTimestamp(jsonObject.get(TIMESTAMP).toString());
            foundAuction.setAck(jsonObject.get(ACK).toString());
            while (it.hasNext()) {
                String key = (String) it.next();
                Log.d("jsonKey ", key);

                switch (key) {
                    case BID:
                        jPrice = jItem.getJSONObject(BID);
                        auctionBid = jPrice.get(VALUE).toString();
                        auctionCurrencyID = jPrice.get(CURRENCY).toString();
                        foundAuction.setBid(auctionBid);
                        foundAuction.setCurrency(auctionCurrencyID); break;
                    case PRICE:
                        jPrice = jItem.getJSONObject(PRICE);
                        auctionPrice = jPrice.get(VALUE).toString();
                        auctionCurrencyID = jPrice.get(CURRENCY).toString();
                        foundAuction.setPrice(auctionPrice);
                        foundAuction.setCurrency(auctionCurrencyID); break;
                    case PICTURE_URL:
                        jPic = jItem.getJSONArray(PICTURE_URL);
                        foundAuction.setPicture(jPic.getString(0));
                        new GetAuctionPic().execute(foundAuction.getPicture()); break;
                    case TITLE:
                        foundAuction.setTitle(jItem.get(TITLE).toString()); break;
                    case ITEM_ID:
                        foundAuction.setItemId(jItem.get(ITEM_ID).toString()); break;
                    case END_TIME:
                        foundAuction.setEndTime(jItem.get(END_TIME).toString()); break;
                    case AUCTION_URL:
                        foundAuction.setUrl(jItem.get(AUCTION_URL).toString()); break;
                    case LISTING_TYPE:
                        foundAuction.setListingType(jItem.get(LISTING_TYPE).toString()); break;
                    case LOCATION:
                        foundAuction.setLocation(jItem.get(LOCATION).toString()); break;
                    case CATEGORY:
                        foundAuction.setCategory(jItem.get(CATEGORY).toString()); break;
                    case CATEGORY_ID:
                        foundAuction.setCategoryId(jItem.get(CATEGORY_ID).toString()); break;
                    case STATUS:
                        foundAuction.setStatus(jItem.get(STATUS).toString()); break;
                    case COUNTRY:
                        foundAuction.setLocation(jItem.get(COUNTRY).toString()); break;
                }
            }
            auctionList.add(foundAuction);
        }
    }

    class GetAuctionPic extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... params) {

            Bitmap pic = null;
            Log.d("jk", params[0]);

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
            ImageView picAuctionIv = (ImageView) findViewById(R.id.picture_found);

            if (pic != null) {
                picBmp = Bitmap.createBitmap(pic);
                assert picAuctionIv != null;
                picAuctionIv.setImageBitmap(picBmp);
            }
        }
    }
}
