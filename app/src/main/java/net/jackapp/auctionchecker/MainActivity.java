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
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity {

    private final String JSON_DB_NAME = "auctionDB.json";

    String itemId, picURL, title, price, bid, url, jsonDBString;
    Bitmap picBmp;
    Typeface caviar, caviarBold;

    FileWorker fileWorker = new FileWorker();
    TextView urlTv, priceTv, pasteBtn, titleTv, bidTv;
    ListView listAuctionLv;

    public static ArrayAdapter<String> auctionAdapter;

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
        } catch (JSONException e) {
            e.printStackTrace();
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

    private void analyzeClipUrl(){
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

        if(clipboardManager.hasPrimaryClip()){
            ClipData.Item clpItem = clipboardManager.getPrimaryClip().getItemAt(0);
            if(clpItem != null){
                String urlToCheck = clpItem.getText().toString();
                urlTv.setText(urlToCheck);
                itemId = getAuctionId(urlToCheck);
            }
        }


        new GetAuctionJSON().execute();
    }

    private String getAuctionId(String url){

        Uri urlToParse = Uri.parse(url);
        String protocol = urlToParse.getScheme();
        String server = urlToParse.getAuthority();
        String path = urlToParse.getPath();
        String[] pathArr = path.split("/");

        return pathArr[pathArr.length-1];

    }

    class GetAuctionJSON extends AsyncTask<Void, Void, Void>{

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
        String auctionCurrencyID = "";
        String dataTypeJson = "JSON";
        String siteId = "0";
        String query_type = "GetSingleItem";
        String version = "515";


        @Override
        protected Void doInBackground(Void... params) {

            try {

                Log.d("jk", "0");
                Uri builtUri = Uri.parse(EBAY_SHOPPING_URL).buildUpon()
                        .appendQueryParameter(QUERY_PARAM, query_type)
                        .appendQueryParameter(FORMAT_PARAM, dataTypeJson)
                        .appendQueryParameter(APPID_PARAM, APP_ID)
                        .appendQueryParameter(SITEID_PARAM, siteId)
                        .appendQueryParameter(VERSION_PARAM, version)
                        .appendQueryParameter(ITEMID_PARAM, itemId).build();

                URL urlToCheck = new URL(builtUri.toString());

                HttpURLConnection urlConnection = (HttpURLConnection) urlToCheck.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"),8);
                StringBuilder sb = new StringBuilder();

                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                jsonString = sb.toString();

                JSONObject jObject = new JSONObject(jsonString);
                getAuctionItem(jObject);

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

            setJsonDataToView();

        }

        private void setJsonDataToView(){
            titleTv.setText(title);
            priceTv.setText(price);
        }

        protected void getAuctionItem(JSONObject jsonObject) throws JSONException {

            final String ITEM = "Item";
            final String BID = "ConvertedBuyItNowPrice";
            final String PRICE = "ConvertedCurrentPrice";
            final String PIC_URL = "PictureURL";
            final String TITLE = "Title";
            final String VALUE = "Value";
            final String CURRENCY = "CurrencyID";

            JSONObject jItem = jsonObject.getJSONObject(ITEM);
            Iterator it = jItem.keys();
            JSONObject jPrice;
            JSONArray jPic;
            JSONObject jTitle;

            while(it.hasNext()){
                String key = (String) it.next();
                Log.d("jsonKey ", key);
                switch (key){
                    case BID :
                        jPrice = jItem.getJSONObject(BID);
                        auctionPrice = jPrice.get(VALUE).toString();
                        auctionCurrencyID = jPrice.get(CURRENCY).toString();
                        bid = "Licytacja: \n" + auctionPrice + " " + auctionCurrencyID + "\n";
                        break;
                    case PRICE :
                        jPrice = jItem.getJSONObject(PRICE);
                        auctionPrice = jPrice.get(VALUE).toString();
                        auctionCurrencyID = jPrice.get(CURRENCY).toString();
                        price = "Kup teraz: \n" + auctionPrice + " " + auctionCurrencyID;
                        break;
                    case PIC_URL :
                        jPic = jItem.getJSONArray(PIC_URL);
                        picURL = jPic.getString(0);
                        new GetAuctionPic().execute(picURL);
                        break;
                    case TITLE :
                        title = jItem.get(TITLE).toString();
                }
            }

        }
    }


    class GetAuctionPic extends AsyncTask<String,Void,Bitmap>{

        @Override
        protected Bitmap doInBackground(String... params) {

            Bitmap pic = null;
            Log.d("jk", params[0]);

            try{

                pic = BitmapFactory.decodeStream((InputStream) new URL(params[0]).getContent());

            } catch (IOException e) {
                e.printStackTrace();
            }

            return pic;
        }

        @Override
        protected void onPostExecute(Bitmap pic) {
            super.onPostExecute(pic);
            ImageView picAuctionIV = (ImageView) findViewById(R.id.picture_found);

            if(pic != null){
                picBmp = Bitmap.createBitmap(pic);
                assert picAuctionIV != null;
                picAuctionIV.setImageBitmap(picBmp);
            }

        }

    }
}
