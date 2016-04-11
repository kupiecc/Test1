package net.jackapp.auctionchecker;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity {

    //    public static ArrayAdapter<String> auctionsAdapter;
    private final String JSON_DB_NAME = "auctionDB.json";
    private final String ITEM = "Item";
    private final String BID = "ConvertedCurrentPrice";
    private final String PICTURE_URL = "PictureURL";
    private final String PRICE = "ConvertedBuyItNowPrice";
    private final String TITLE = "Title";
    private final String VALUE = "Value";
    private final String CURRENCY = "CurrencyID";
    private final String ITEM_ID = "ItemID";
    private final String VERSION = "Version";
    private final String END_TIME = "EndTime";
    private final String AUCTION_URL = "ViewItemURLForNaturalSearch";
    private final String LISTING_TYPE = "ListingType";
    private final String LOCATION = "Location";
    private final String CATEGORY = "PrimaryCategoryName";
    private final String CATEGORY_ID = "PrimaryCategoryID";
    private final String STATUS = "ListingStatus";
    private final String COUNTRY = "Country";
    private final String TIMESTAMP = "Timestamp";
    private final String ACK = "Ack";

    ArrayList<Auction> auctionsList;
    AuctionAdapter auctionsAdapter;
    Auction foundAuction;
    Bitmap picBmp;
    FileWorker fileWorker = new FileWorker();
    ImageView picAuctionIv;
    JSONArray auctionDBArray = new JSONArray();
    JSONObject countriesDBJson, jItem;
    ListView listAuctionsLv;
    RelativeLayout foundLayout;
    String itemId, auctionDBString, siteExtension, countriesDBString;
    TextView urlTv, priceTv, pasteBtn, titleTv, bidTv;
    DecimalFormat df;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        initViewObject();
        loadCountriesCode();

        try {
            readDB();
            loadJsonToLv();
        } catch (JSONException e) {
            Log.e("jk Error", e.toString());
        }
    }

    public void onPasteAuctionLink(View view) {

        urlTv.setTextColor(Color.parseColor("#ffffff"));
        analyzeClipUrl();

    }

    public void saveAuction(View view) throws JSONException {

        JSONObject itemJson = new JSONObject();

        if (Auction.auctionExist(foundAuction.getItemId(), auctionDBArray)) {
            Toast.makeText(this, "This auction exist.", Toast.LENGTH_LONG).show();
        } else {

            try {

                Iterator it = jItem.keys();
                while (it.hasNext()) {

                    String key = (String) it.next();
                    itemJson.put(key, jItem.get(key));

                }
                auctionDBArray.put(itemJson);

//                itemJson.put(ITEM_ID, foundAuction.getItemId());
//                itemJson.put(END_TIME, foundAuction.getEndTime());
//                itemJson.put(AUCTION_URL, foundAuction.getUrl());
//                itemJson.put(LOCATION, foundAuction.getLocation());
//                itemJson.put(PICTURE_URL, foundAuction.getPicture());
//                itemJson.put(CATEGORY_ID, foundAuction.getCategoryId());
//                itemJson.put(CATEGORY, foundAuction.getCategory());
//                itemJson.put(PRICE, foundAuction.getPrice());
//                itemJson.put(BID, foundAuction.getBid());
//                itemJson.put(CURRENCY, foundAuction.getCurrency());
//                itemJson.put(STATUS, foundAuction.getStatus());
//                itemJson.put(TITLE, foundAuction.getTitle());
//                itemJson.put(COUNTRY, foundAuction.getCountry());
//
//
//
//                auctionDBArray.put(itemJson);

                Log.d("jk saveItem", auctionDBArray.toString());

                fileWorker.writeJsonFile(this, auctionDBArray.toString(), JSON_DB_NAME);
                loadJsonToLv();
                clearFoundItem();

            } catch (JSONException e) {
                Log.d("jk", "saveAuction function problem: " + e.toString());
                e.printStackTrace();
            }
        }
    }

    public void closeFoundItem(View view) {

        clearFoundItem();

    }

    private void loadCountriesCode() {

        try {
            countriesDBString = fileWorker.readFile(this, "countriesCode.json", this.getAssets());
            countriesDBJson = new JSONObject(countriesDBString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void clearFoundItem() {
        titleTv.setText("");
        picAuctionIv.setImageResource(R.drawable.no_img);
        priceTv.setText("");
        bidTv.setText("");
        urlTv.setText("");
        urlTv.setHint("Paste auction link.");
        foundLayout.setVisibility(View.GONE);
    }

    private void readDB() throws JSONException {
        try {
            auctionDBString = fileWorker.readFile(this, JSON_DB_NAME);
            if (!auctionDBString.equals(""))
                auctionDBArray = new JSONArray(auctionDBString);
            else
                auctionDBArray = new JSONArray();
        } catch (JSONException e) {
            Log.d("function readDB", e.toString());
        }
        Log.d("readDB", auctionDBString);

    }

    private String getAuctionId(String url) {

        Uri urlToParse = Uri.parse(url);
        String lastPathSegment = urlToParse.getLastPathSegment();
        siteExtension = urlToParse.getAuthority();
        siteExtension = siteExtension.substring(siteExtension.lastIndexOf(".") + 1);

        return lastPathSegment;

    }

    private void analyzeClipUrl() {
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

        if (clipboardManager.hasPrimaryClip()) {
            ClipData.Item clpItem = clipboardManager.getPrimaryClip().getItemAt(0);
            if (clpItem != null) {
                String urlToCheck = clpItem.getText().toString();
                urlTv.setText(urlToCheck);
                itemId = getAuctionId(urlToCheck);
                foundLayout = (RelativeLayout) findViewById(R.id.found_layout);
                assert foundLayout != null;
                foundLayout.setVisibility(View.VISIBLE);
                new GetAuctionJSON().execute();
            }
        } else {
            Toast.makeText(this, "Copy Ebay link and paste.", Toast.LENGTH_LONG).show();
        }


    }

    private void loadJsonToLv() throws JSONException {

        auctionsList = new ArrayList<Auction>();
        auctionsList.clear();
        auctionsAdapter = new AuctionAdapter(getApplicationContext(), auctionsList);
        ArrayList<Auction> newAuctions = Auction.fromJson(auctionDBArray);
        auctionsAdapter.clear();
        auctionsAdapter.addAll(newAuctions);
        listAuctionsLv.setAdapter(auctionsAdapter);

        listAuctionsLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent showItemIntent = new Intent(getApplicationContext(), AuctionView.class);
                Auction auctionToView = auctionsAdapter.getItem(position);
                showItemIntent.putExtra("auction", auctionToView);
                startActivity(showItemIntent);
            }
        });

    }

    private void initViewObject() {
        urlTv = (TextView) findViewById(R.id.url_auction);
        priceTv = (TextView) findViewById(R.id.price_found);
        pasteBtn = (TextView) findViewById(R.id.paste_btn);
        titleTv = (TextView) findViewById(R.id.title_found);
        bidTv = (TextView) findViewById(R.id.bid_found);
        listAuctionsLv = (ListView) findViewById(R.id.list_auctions);
        picAuctionIv = (ImageView) findViewById(R.id.picture_found);

        df = new DecimalFormat("###,###.00");

        registerForContextMenu(listAuctionsLv);
    }

    private JSONArray removeAuction(JSONArray db, String auctionID) {
        JSONArray newDb = new JSONArray();
        for (int i = 0; i < db.length(); i++) {
            try {
                if (db.getJSONObject(i).getString(ITEM_ID) != auctionID) {
                    newDb.put(db.getJSONObject(i));
//                    Toast.makeText(this, db.getJSONObject(i).toString(), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return newDb;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.auctions_context_menu, menu);
        menu.setHeaderTitle("Do: ");

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        String titleContextItem = auctionsAdapter.getItem(info.position).getTitle();
        String idContextItem = auctionsAdapter.getItem(info.position).getItemId();
        item.setTitle("aaaa");
        switch (item.getItemId()) {
            case R.id.delete_id:

                auctionDBArray = removeAuction(auctionDBArray, idContextItem);
                fileWorker.writeJsonFile(this, auctionDBArray.toString(), JSON_DB_NAME);
                auctionsList.remove(info.position);
                auctionsAdapter.notifyDataSetChanged();
                Toast.makeText(this, titleContextItem + " removed.", Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onContextItemSelected(item);

        }
//        return super.onContextItemSelected(item);

    }

    private String siteExt(String ext) throws JSONException {
        String siteId = "0";
        siteId = countriesDBJson.getString(ext);
        return siteId;
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    class GetAuctionJSON extends AsyncTask<Void, Void, Void> {

        final String EBAY_SHOPPING_URL = "http://open.api.ebay.com/shopping?";
        final String APP_ID = "Individu-PriceChe-PRD-53a0284bf-5b5a8f9b";
        final String QUERY_PARAM = "callname";
        final String FORMAT_PARAM = "responseencoding";
        final String APPID_PARAM = "appid";
        final String SITEID_PARAM = "siteid";
        final String VERSION_PARAM = "version";
        final String ITEMID_PARAM = "itemID";

        String jsonString = "";
        String auctionPrice = "";
        String auctionBid = "";
        String auctionCurrencyID = "";
        String dataTypeJson = "JSON";
        String siteId = "com";
        String query_type = "GetSingleItem";
        String version = "515";


        @Override
        protected Void doInBackground(Void... params) {

            try {
                siteId = siteExt(siteExtension);
                Uri builtUri = Uri.parse(EBAY_SHOPPING_URL).buildUpon().appendQueryParameter(QUERY_PARAM, query_type).appendQueryParameter(FORMAT_PARAM, dataTypeJson).appendQueryParameter(APPID_PARAM, APP_ID).appendQueryParameter(SITEID_PARAM, siteId).appendQueryParameter(VERSION_PARAM, version).appendQueryParameter(ITEMID_PARAM, itemId).build();

                URL urlToCheck = new URL(builtUri.toString());
                Log.d("jk", builtUri.toString());

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

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            setFoundAuctionToView(foundAuction);
        }

        private void setFoundAuctionToView(Auction fa) {
            titleTv.setText(fa.getTitle());
            if (fa.getPrice() != null && fa.getPrice() != "-") {
                String priceFormat = df.format(Double.valueOf(fa.getPrice())) + " " + fa.getCurrency() + "\n(Buy now)";
                priceTv.setText(priceFormat);
            } else {
                priceTv.setText("-");
            }

            if (fa.getBid() != null && fa.getBid() != "-") {
                String bidFormat = df.format(Double.valueOf(fa.getBid())) + " " + fa.getCurrency() + "\n(Bid)";
                bidTv.setText(bidFormat);
            } else {
                bidTv.setText("-");
            }
        }

        protected void getAuctionItem(String jsonString) throws JSONException {

            foundAuction = new Auction();

            JSONObject jsonObject = new JSONObject(jsonString);

            jItem = jsonObject.getJSONObject(ITEM);
            Iterator it = jItem.keys();
            JSONObject jPrice;
            JSONArray jPic;

            foundAuction.setVersion(jsonObject.get(VERSION).toString());
            foundAuction.setTimestamp(jsonObject.get(TIMESTAMP).toString());
            foundAuction.setAck(jsonObject.get(ACK).toString());
            while (it.hasNext()) {
                String key = (String) it.next();

                switch (key) {
                    case BID:
                        jPrice = jItem.getJSONObject(BID);
                        Log.d("jk has bid ", String.valueOf(jItem.has(BID)));
                        auctionBid = jPrice.get(VALUE).toString();
                        auctionCurrencyID = jPrice.get(CURRENCY).toString();
                        foundAuction.setBid(auctionBid);
                        foundAuction.setCurrency(auctionCurrencyID);
                        break;
                    case PRICE:
                        jPrice = jItem.getJSONObject(PRICE);
                        Log.d("jk has price ", String.valueOf(jItem.has(BID)));
                        auctionPrice = jPrice.get(VALUE).toString();
                        auctionCurrencyID = jPrice.get(CURRENCY).toString();
                        foundAuction.setPrice(auctionPrice);
                        foundAuction.setCurrency(auctionCurrencyID);
                        break;
                    case PICTURE_URL:
                        jPic = jItem.getJSONArray(PICTURE_URL);
                        foundAuction.setPicture(jPic.getString(0));
                        new GetAuctionPic().execute(foundAuction.getPicture());
                        break;
                    case TITLE:
                        foundAuction.setTitle(jItem.get(TITLE).toString());
                        break;
                    case ITEM_ID:
                        foundAuction.setItemId(jItem.get(ITEM_ID).toString());
                        break;
                    case END_TIME:
                        foundAuction.setEndTime(jItem.get(END_TIME).toString());
                        break;
                    case AUCTION_URL:
                        foundAuction.setUrl(jItem.get(AUCTION_URL).toString());
                        break;
                    case LISTING_TYPE:
                        foundAuction.setListingType(jItem.get(LISTING_TYPE).toString());
                        break;
                    case LOCATION:
                        foundAuction.setLocation(jItem.get(LOCATION).toString());
                        break;
                    case CATEGORY:
                        foundAuction.setCategory(jItem.get(CATEGORY).toString());
                        break;
                    case CATEGORY_ID:
                        foundAuction.setCategoryId(jItem.get(CATEGORY_ID).toString());
                        break;
                    case STATUS:
                        foundAuction.setStatus(jItem.get(STATUS).toString());
                        break;
                    case COUNTRY:
                        foundAuction.setLocation(jItem.get(COUNTRY).toString());
                        break;
                }
            }
        }
    }

    class GetAuctionPic extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... params) {

            Bitmap pic = null;
//            Log.d("jk", params[0]);

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
                picBmp = Bitmap.createBitmap(pic);
                assert picAuctionIv != null;
                picAuctionIv.setImageBitmap(picBmp);
            }
        }
    }
}
