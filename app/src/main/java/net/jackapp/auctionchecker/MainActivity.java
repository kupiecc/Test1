package net.jackapp.auctionchecker;

import android.app.Activity;
import android.app.Dialog;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
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
import java.net.URL;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity {

    //    public static ArrayAdapter<String> auctionsAdapter;
    private final String JSON_DB_NAME = "auctionDB.json";
    private final String ITEM = "Item";
    private final String PRICE = "ConvertedCurrentPrice";
    private final String PICTURE_URL = "PictureURL";
    private final String BUYITNOW = "ConvertedBuyItNowPrice";
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
    private final String HISTORY = "History";
    private final String HISTORYBID = "Bid";
    private final String HISTORYBUYITNOW = "BuyItNow";
    private final String HISTORYPRICE = "Price";
    private final String HISTORYDATE = "Date";
    private final String CREATE_AT = "CreateAt";
    private final String NO_BUY_IT = "NoBuyIt";

    private final String EBAY_SHOPPING_URL = "http://open.api.ebay.com/shopping?";
    private final String APP_ID = "Individu-PriceChe-PRD-53a0284bf-5b5a8f9b";
    private final String QUERY_PARAM = "callname";
    private final String FORMAT_PARAM = "responseencoding";
    private final String APPID_PARAM = "appid";
    private final String SITEID_PARAM = "siteid";
    private final String VERSION_PARAM = "version";
    private final String ITEMID_PARAM = "itemID";

    String jsonString = "";
    String dataTypeJson = "JSON";
    String siteId = "com";
    String query_type = "GetSingleItem";
    String version = "515";

    ArrayList<Auction> auctionsList;
    AuctionAdapter auctionsAdapter;
    Auction foundAuction, auctionAnalyzeEbay, auctionAnalyzeApp;
    Bitmap picBmp;
    FileWorker fileWorker = new FileWorker();
    ImageView picAuctionIv;
    Integer auctionCount = 0, auctionCheckNum;
    JSONArray auctionsJsonArr;
    JSONObject countriesDBJson, jItem, analyzeJsonItem;
    JsonWorker jsonWorker = new JsonWorker();
    ListView listAuctionsLv;
    LinearLayout infoListLayout;
    LayoutInflater layoutInflater;
    ScrollView infoSv;
    String auctionMsg;
    RelativeLayout foundLayout, infoLayout;
    String itemId, auctionDBString, siteExtension, countriesDBString, testString;
    TextView urlTv, priceTv, pasteButtonTv, titleTv, bidTv, updateButtonTv, closeInfoButtonTv, saveBtnTv, clearBtnTv, infoTitleTv;
    View infoItem, infoItemEnd;
    DecimalFormat decimalFormat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        decimalFormat = new DecimalFormat("###.00");
        decimalFormat.setMaximumFractionDigits(2);

        initViewObject();
        loadCountriesCode();

        try {
            auctionsJsonArr = jsonWorker.readFileToJsonArray(this, JSON_DB_NAME);
            System.out.println("auctionsJsonArr = " + auctionsJsonArr);
            loadJsonToLv();
        } catch (JSONException e) {
            Log.e("onCreate: ", e.toString());
        }
    }

    private void loadJsonToLv() throws JSONException {

        auctionsList = new ArrayList<Auction>();
        auctionsList.clear();
        auctionsAdapter = new AuctionAdapter(getApplicationContext(), auctionsList);
        ArrayList<Auction> newAuctions = Auction.loadFromJsonArray(auctionsJsonArr);
        auctionsAdapter.clear();
        auctionsAdapter.addAll(newAuctions);
        listAuctionsLv.setAdapter(auctionsAdapter);
        listAuctionsLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent showItemIntent = new Intent(getApplicationContext(), AuctionView.class);
                Auction auctionToView = auctionsAdapter.getItem(position);
                showItemIntent.putExtra("auctionToView", auctionToView);
                showItemIntent.putExtra("history", auctionToView.getHistoryString());

                System.out.println("auctionToView.getHistoryString() = " + auctionToView.getHistoryString());
                startActivity(showItemIntent);
            }
        });

    }

    private void initViewObject() {
        urlTv = (TextView) findViewById(R.id.url_auction);
        priceTv = (TextView) findViewById(R.id.price_found);
        pasteButtonTv = (TextView) findViewById(R.id.paste_button_tv);
        titleTv = (TextView) findViewById(R.id.title_found);
        bidTv = (TextView) findViewById(R.id.bid_found);
        listAuctionsLv = (ListView) findViewById(R.id.list_auctions);
        picAuctionIv = (ImageView) findViewById(R.id.picture_found);
        infoLayout = (RelativeLayout) findViewById(R.id.info);
        updateButtonTv = (TextView) findViewById(R.id.update_button_tv);
        saveBtnTv = (TextView) findViewById(R.id.save_auction_btn_tv);
        clearBtnTv = (TextView) findViewById(R.id.clear_view_btn_tv);

        registerForContextMenu(listAuctionsLv);
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
        switch (item.getItemId()) {
            case R.id.delete_id:
                auctionsJsonArr = jsonWorker.removeAuctionFromJson(auctionsJsonArr, idContextItem);
                fileWorker.writeJsonFile(this, auctionsJsonArr.toString(), JSON_DB_NAME);
                auctionsList.remove(info.position);
                auctionsAdapter.notifyDataSetChanged();
                Toast.makeText(this, titleContextItem + " removed.", Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onContextItemSelected(item);

        }
//        return super.onContextItemSelected(item);
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

    private void loadCountriesCode() {

        try {
            countriesDBString = fileWorker.readFile(this, "countriesCode.json", this.getAssets());
            countriesDBJson = new JSONObject(countriesDBString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void onPasteAuctionLink(View view) {

        saveBtnTv.setVisibility(View.GONE);
        clearBtnTv.setVisibility(View.GONE);
        urlTv.setTextColor(Color.parseColor("#ffffff"));
        view.setClickable(false);
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
                foundLayout = (RelativeLayout) findViewById(R.id.found_layout);
                assert foundLayout != null;
                foundLayout.setVisibility(View.VISIBLE);
                new GetAuctionJSON().execute();
            }
        } else {
            Toast.makeText(this, "Copy Ebay link and paste.", Toast.LENGTH_LONG).show();
        }
    }

    public void saveAuction(View view) throws JSONException {

        JSONObject itemJson = new JSONObject();

        if (Auction.auctionExist(foundAuction.getItemId(), auctionsJsonArr)) {
            Toast.makeText(this, "This auction exist.", Toast.LENGTH_LONG).show();
        } else {

            try {

                Iterator it = jItem.keys();
                while (it.hasNext()) {

                    String key = (String) it.next();
                    itemJson.put(key, jItem.get(key));
//                    Log.d(key, jItem.get(key).toString());


                }

                itemJson.put("SiteExtension", foundAuction.getSiteExt());
                itemJson.put("SiteID", foundAuction.getSiteId());
                itemJson.put("UrlJson", foundAuction.getUrlJson());
                itemJson.put(CREATE_AT, getNowDate());

                JSONArray historyArr = new JSONArray();
                JSONObject historyObj = new JSONObject();
                historyObj.put(HISTORYDATE, getNowDate());
                if (foundAuction.getPrice() != null) {
                    Number bidFormat = NumberFormat.getInstance().parse(foundAuction.getBid());
                    Number priceFormat = NumberFormat.getInstance().parse(foundAuction.getPrice());
                    historyObj.put(HISTORYBID, bidFormat.floatValue());
                    historyObj.put(HISTORYBUYITNOW, priceFormat.floatValue());
                } else {
                    Number bidFormat = NumberFormat.getInstance().parse(foundAuction.getBid());
                    historyObj.put(HISTORYPRICE, bidFormat.floatValue());
                }
                historyArr.put(historyObj);
                itemJson.put(HISTORY, historyArr);
                auctionsJsonArr.put(itemJson);


                Log.d("jk saveItem", auctionsJsonArr.toString());

                fileWorker.writeJsonFile(this, auctionsJsonArr.toString(), JSON_DB_NAME);
                loadJsonToLv();
                clearFoundItem();

            } catch (JSONException e) {
                Log.d("jk", "saveAuction function problem: " + e.toString());
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    public void closeFoundItem(View view) {

        clearFoundItem();

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

    private String getAuctionId(String url) {

        Uri urlToParse = Uri.parse(url);
        String lastPathSegment = urlToParse.getLastPathSegment();
        siteExtension = urlToParse.getAuthority();
        siteExtension = siteExtension.substring(siteExtension.lastIndexOf(".") + 1);

        return lastPathSegment;
    }

    private String siteExt(String ext) throws JSONException {
        String siteId = "0";
        siteId = countriesDBJson.getString(ext);
        return siteId;
    }

    private String getNowDate() {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd, HH:mm");
        String date = df.format(Calendar.getInstance().getTime());
        return date;
    }

    public void onUpdateAuctions(View view) {

        view.setClickable(false);
        final Dialog dialog = new Dialog(this, R.style.FullHeightDialog);
        dialog.setContentView(R.layout.info);
        closeInfoButtonTv = (TextView) dialog.findViewById(R.id.close_info_tv);
        infoListLayout = (LinearLayout) dialog.findViewById(R.id.info_tv_list);
        infoTitleTv = (TextView) dialog.findViewById(R.id.title_info_tv);
        auctionCount = auctionsJsonArr.length() - 1;
        checkAuction();
        closeInfoButtonTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.hide();
            }
        });
        dialog.show();
    }

    private void checkAuction() {
        System.out.println(" checkAuctions = " + auctionCount);
        auctionCheckNum = auctionsJsonArr.length() - auctionCount;
        auctionMsg = "Check auction: " + String.valueOf(auctionCheckNum) + "/" + auctionsJsonArr.length();
        infoTitleTv.setText(auctionMsg);
        new AnalyzeAuctions().execute(auctionCount);
        auctionCount = auctionCount - 1;
    }

    class AnalyzeAuctions extends AsyncTask<Integer, Void, Void> {

        final String URL_JSON = "UrlJson";

        @Override
        protected Void doInBackground(Integer... params) {

            try {
                Integer auctionNum = params[0];
                URL urlToCheck = new URL(auctionsJsonArr.getJSONObject(auctionNum).get(URL_JSON).toString());
                getEbayAuctionItem(getEbayJson(urlToCheck));
                getAppAuctionItem(auctionNum);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            String infoBid, infoBuyNow, infoPrice;
            String infoTitle = auctionAnalyzeEbay.getTitle();
            final String infoUrl = auctionAnalyzeEbay.getUrl();

            //Ebay
            String buyItNowEbay = auctionAnalyzeEbay.getPrice() != null ? auctionAnalyzeEbay.getPrice() : "";
            String bidEbay = auctionAnalyzeEbay.getBid();
            String statusEbay = auctionAnalyzeEbay.getStatus();
            String endTimeEbay = auctionAnalyzeEbay.getEndTime();

            //App
            String buyNowApp = auctionAnalyzeApp.getPrice() != null ? auctionAnalyzeApp.getPrice() : "";
            String bidApp = auctionAnalyzeApp.getBid();
            String idApp = auctionAnalyzeApp.getItemId();

            layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            infoItem = layoutInflater.inflate(R.layout.info_item, null);
            infoItemEnd = layoutInflater.inflate(R.layout.info_item_end, null);
            TextView titleInfoItemTv = (TextView) infoItem.findViewById(R.id.info_auction_title_tv);
            TextView priceInfoItemTv = (TextView) infoItem.findViewById(R.id.info_auction_price_tv);
            TextView bidInfoItemTv = (TextView) infoItem.findViewById(R.id.info_auction_bid_tv);
            TextView urlInfoBtn = (TextView) infoItem.findViewById(R.id.info_url_btn);


            urlInfoBtn.setText("Show site");
            urlInfoBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent auctionSiteIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(infoUrl));
                    startActivity(auctionSiteIntent);
                }
            });

            titleInfoItemTv.setText(String.valueOf(auctionCheckNum) + ". " + infoTitle);

            try {
                if (buyItNowEbay.equals("") || buyNowApp.equals("")) {

                    if (bidEbay.equals(bidApp))
                        infoPrice = "Price: No changes";
                    else {
                        infoPrice = "New price: " + bidEbay + " (old: " + bidApp + ")";
                        updateAuction(idApp, decimalFormat.parse(bidEbay).floatValue(), NO_BUY_IT);
                    }
                    bidInfoItemTv.setVisibility(View.GONE);
                    priceInfoItemTv.setText(infoPrice);
                } else {
                    bidInfoItemTv.setVisibility(View.VISIBLE);

                    if (!buyNowApp.equals(buyItNowEbay) && !bidApp.equals(bidEbay)) {

                        infoBuyNow = "Price: " + buyItNowEbay + " (old: " + buyNowApp + ")";
                        infoBid = "Bid: " + bidEbay + " (old: " + bidApp + ")";

                        updateAuction(idApp, decimalFormat.parse(bidEbay).floatValue(), decimalFormat.parse(buyItNowEbay).floatValue());

                    } else if (buyNowApp.equals(buyItNowEbay) && !bidApp.equals(bidEbay)) {
                        infoBuyNow = "Price: No changes";
                        infoBid = "Bid: " + bidEbay + " (old: " + bidApp + ")";
                        updateAuction(idApp, decimalFormat.parse(bidEbay).floatValue(), PRICE);
                    } else if (!buyNowApp.equals(buyItNowEbay) && bidApp.equals(bidEbay)) {
                        infoBid = "Bid No changes";
                        infoBuyNow = "Price: " + buyItNowEbay + " (old: " + buyNowApp + ")";
                        updateAuction(idApp, decimalFormat.parse(buyItNowEbay).floatValue(), BUYITNOW);
                    } else {
                        infoBid = "Bid No changes";
                        infoBuyNow = "Price: No changes";
                    }
                    priceInfoItemTv.setText(infoBuyNow);
                    bidInfoItemTv.setText(infoBid);
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }
            updateAuction(idApp, statusEbay, STATUS);
            updateAuction(idApp, endTimeEbay, END_TIME);
            infoListLayout.addView(infoItem);
            if (auctionCount >= 0) {
                checkAuction();
            } else {
                infoListLayout.addView(infoItemEnd);
                updateButtonTv.setClickable(true);
            }

        }

        protected void updateAuction(String id, String value, String name) {
            for (int i = 0; i < auctionsJsonArr.length(); i++) {
                try {
                    if (auctionsJsonArr.getJSONObject(i).get(ITEM_ID).toString() == id) {
                        switch (name) {
                            case STATUS:
                                auctionsJsonArr.getJSONObject(i).put(STATUS, value);
                                fileWorker.writeJsonFile(getApplicationContext(), auctionsJsonArr.toString(), JSON_DB_NAME);
                                break;
                            case END_TIME:
                                auctionsJsonArr.getJSONObject(i).put(END_TIME, value);
                                fileWorker.writeJsonFile(getApplicationContext(), auctionsJsonArr.toString(), JSON_DB_NAME);
                                break;


                        }
                        loadJsonToLv();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        protected void updateAuction(String id, Float bid, Float buyItNow) {
            for (int i = 0; i < auctionsJsonArr.length(); i++) {
                try {
                    if (auctionsJsonArr.getJSONObject(i).get(ITEM_ID).toString() == id) {
                        JSONObject historyObj = new JSONObject();
                        auctionsJsonArr.getJSONObject(i).getJSONObject(BUYITNOW).put(VALUE, buyItNow);
                        auctionsJsonArr.getJSONObject(i).getJSONObject(PRICE).put(VALUE, bid);
                        historyObj.put(HISTORYBUYITNOW, buyItNow);
                        historyObj.put(HISTORYBID, bid);
                        historyObj.put(HISTORYDATE, getNowDate());
                        auctionsJsonArr.getJSONObject(i).getJSONArray(HISTORY).put(historyObj);
                        fileWorker.writeJsonFile(getApplicationContext(), auctionsJsonArr.toString(), JSON_DB_NAME);
                        loadJsonToLv();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        protected void updateAuction(String id, Float value, String name) {

            for (int i = 0; i < auctionsJsonArr.length(); i++) {
                try {
                    System.out.println("value = " + value);
                    if (auctionsJsonArr.getJSONObject(i).get(ITEM_ID).toString() == id) {
                        JSONObject historyObj = new JSONObject();
                        switch (name) {
                            case BUYITNOW:
                                auctionsJsonArr.getJSONObject(i).getJSONObject(BUYITNOW).put(VALUE, value);
                                historyObj.put(HISTORYBUYITNOW, value);
                                historyObj.put(HISTORYBID, auctionsJsonArr.getJSONObject(i).getJSONObject(HISTORY).get(HISTORYBID));
                                historyObj.put(HISTORYDATE, getNowDate());
                                auctionsJsonArr.getJSONObject(i).getJSONArray(HISTORY).put(historyObj);
                                break;
                            case PRICE:
                                auctionsJsonArr.getJSONObject(i).getJSONObject(PRICE).put(VALUE, value);
                                historyObj.put(HISTORYBUYITNOW, auctionsJsonArr.getJSONObject(i).getJSONObject(HISTORY).get(HISTORYBUYITNOW));
                                historyObj.put(HISTORYBID, value);
                                historyObj.put(HISTORYDATE, getNowDate());
                                auctionsJsonArr.getJSONObject(i).getJSONArray(HISTORY).put(historyObj);
                                break;
                            case NO_BUY_IT:
                                auctionsJsonArr.getJSONObject(i).getJSONObject(PRICE).put(VALUE, value);
                                historyObj.put(HISTORYPRICE, value);
                                historyObj.put(HISTORYDATE, getNowDate());
                                auctionsJsonArr.getJSONObject(i).getJSONArray(HISTORY).put(historyObj);
                                break;
                        }
                        fileWorker.writeJsonFile(getApplicationContext(), auctionsJsonArr.toString(), JSON_DB_NAME);
                        loadJsonToLv();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }

        protected String getEbayJson(URL url) throws IOException {
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"), 8);
            StringBuilder sb = new StringBuilder();

            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            String analyzeJsonString;
            analyzeJsonString = sb.toString();
            return analyzeJsonString;
        }

        protected void getAppAuctionItem(Integer auctionNum) {
            auctionAnalyzeApp = new Auction();
            try {
                if (auctionsJsonArr.getJSONObject(auctionNum).has(BUYITNOW)) {
                    auctionAnalyzeApp.setPrice(auctionsJsonArr.getJSONObject(auctionNum).getJSONObject(BUYITNOW).get(VALUE).toString());
                }
                auctionAnalyzeApp.setBid(auctionsJsonArr.getJSONObject(auctionNum).getJSONObject(PRICE).get(VALUE).toString());
                auctionAnalyzeApp.setItemId(auctionsJsonArr.getJSONObject(auctionNum).get(ITEM_ID).toString());
                auctionAnalyzeApp.setCurrency(auctionsJsonArr.getJSONObject(auctionNum).getJSONObject(PRICE).get(CURRENCY).toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        protected void getEbayAuctionItem(String jsonString) throws JSONException {

            auctionAnalyzeEbay = new Auction();
            JSONObject jsonObject = new JSONObject(jsonString);
            analyzeJsonItem = jsonObject.getJSONObject(ITEM);
            Iterator it = analyzeJsonItem.keys();

            while (it.hasNext()) {
                String key = (String) it.next();
                switch (key) {
                    case PRICE:
                        auctionAnalyzeEbay.setBid(analyzeJsonItem.getJSONObject(PRICE).get(VALUE).toString());
                        auctionAnalyzeEbay.setCurrency(analyzeJsonItem.getJSONObject(PRICE).get(CURRENCY).toString());
                        break;
                    case BUYITNOW:
                        auctionAnalyzeEbay.setPrice(analyzeJsonItem.getJSONObject(BUYITNOW).get(VALUE).toString());
                        auctionAnalyzeEbay.setCurrency(analyzeJsonItem.getJSONObject(BUYITNOW).get(CURRENCY).toString());
                        break;
                    case TITLE:
                        auctionAnalyzeEbay.setTitle(analyzeJsonItem.get(TITLE).toString());
                        break;
                    case ITEM_ID:
                        auctionAnalyzeEbay.setItemId(analyzeJsonItem.get(ITEM_ID).toString());
                        break;
                    case AUCTION_URL:
                        auctionAnalyzeEbay.setUrl(analyzeJsonItem.get(AUCTION_URL).toString());
                        break;
                    case STATUS:
                        auctionAnalyzeEbay.setStatus(analyzeJsonItem.get(STATUS).toString());
                        break;
//                    case END_TIME:
//                        auctionAnalyzeEbay.setStatus(analyzeJsonItem.get(END_TIME).toString());
//                        break;
                }
            }
        }
    }

    class GetAuctionJSON extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            try {
                foundAuction = new Auction();
                siteId = siteExt(siteExtension);
                foundAuction.setSiteExt(siteExtension);
                foundAuction.setSiteId(siteId);

                Uri builtUri = Uri.parse(EBAY_SHOPPING_URL).buildUpon().appendQueryParameter(QUERY_PARAM, query_type).appendQueryParameter(FORMAT_PARAM, dataTypeJson).appendQueryParameter(APPID_PARAM, APP_ID).appendQueryParameter(SITEID_PARAM, siteId).appendQueryParameter(VERSION_PARAM, version).appendQueryParameter(ITEMID_PARAM, itemId).build();
                foundAuction.setUrlJson(builtUri.toString());
                URL urlToCheck = new URL(builtUri.toString());
                Log.d("ebay json", builtUri.toString());
                HttpURLConnection urlConnection = (HttpURLConnection) urlToCheck.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();

                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                jsonString = sb.toString();
                getAuctionItem(jsonString);

            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            titleTv.setText(foundAuction.getTitle());
            priceTv.setText(foundAuction.getCurrencyPrice());
            bidTv.setText(foundAuction.getCurrencyBid());
            if (foundAuction.getTitle() != null) saveBtnTv.setVisibility(View.VISIBLE);
            clearBtnTv.setVisibility(View.VISIBLE);
            pasteButtonTv.setClickable(true);
        }

        protected void getAuctionItem(String jsonString) throws JSONException {

            JSONObject jsonObject = new JSONObject(jsonString);

            jItem = jsonObject.getJSONObject(ITEM);
            Iterator it = jItem.keys();

            foundAuction.setVersion(jsonObject.get(VERSION).toString());
            foundAuction.setTimestamp(jsonObject.get(TIMESTAMP).toString());
            foundAuction.setAck(jsonObject.get(ACK).toString());

            while (it.hasNext()) {
                String key = (String) it.next();

                switch (key) {
                    case PRICE:
                        foundAuction.setBid(jItem.getJSONObject(PRICE).get(VALUE).toString());
                        foundAuction.setCurrency(jItem.getJSONObject(PRICE).get(CURRENCY).toString());
                        break;
                    case BUYITNOW:
                        foundAuction.setPrice(jItem.getJSONObject(BUYITNOW).get(VALUE).toString());
                        foundAuction.setCurrency(jItem.getJSONObject(BUYITNOW).get(CURRENCY).toString());
                        break;
                    case PICTURE_URL:
                        if (jItem.getJSONArray(PICTURE_URL).length() > 0) {
                            foundAuction.setPicture(jItem.getJSONArray(PICTURE_URL).getString(0));
                            new GetAuctionPic().execute(foundAuction.getPicture());
                        } else {
                            foundAuction.setPicture("");
                        }
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
