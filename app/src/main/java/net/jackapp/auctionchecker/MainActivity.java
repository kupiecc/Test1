package net.jackapp.auctionchecker;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.os.SystemClock;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity {

    private AlarmManager alarmManager;
    private PendingIntent alarmIntent;

    String jsonString = "";
    String dataTypeJson = "JSON";
    String siteId = "com";
    String query_type = "GetSingleItem";
    String version = "515";

    private String[] drawerStringList;

    ArrayList<Auction> auctionsList;
    AuctionAdapter auctionsAdapter;
    AuctionRecyclerAdapter auctionRecyclerAdapter;
    Auction foundAuction, auctionAnalyzeEbay, auctionAnalyzeApp;
    Bitmap picBmp;
    DecimalFormat decimalFormat;
    DrawerLayout drawerLayout;
    FileWorker fileWorker = new FileWorker();
    ImageView picAuctionIv, updateBtn;
    Integer auctionCount = 0, auctionCheckNum;
    JSONArray auctionsJsonArr;
    JSONObject countriesDBJson, jItem, analyzeJsonItem;
    JsonWorker jsonWorker = new JsonWorker();
    LinearLayout infoListLayout, foundLayout;
    ListView drawerLv;
    LayoutInflater layoutInflater;
    String auctionMsg;
    RecyclerView listAuctionsLv;
    RelativeLayout infoLayout;
    String itemId, siteExtension, countriesDBString;
    TextView buyItNowTv, titleTv, priceTv, closeInfoButtonTv, saveBtnTv, clearBtnTv, infoTitleTv;
    Toolbar toolbar;
    View infoItem, infoItemEnd;


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


//        IntentFilter statusIntentFilter = new IntentFilter(Constants.BROADCAST_ACTION);
//        statusIntentFilter.addDataScheme("http");
//        ResponseReciver responseReciver = new ResponseReciver();
//        LocalBroadcastManager.getInstance(this).registerReceiver(responseReciver, statusIntentFilter);

        try {
            auctionsJsonArr = jsonWorker.readFileToJsonArray(this, Constants.JSON_DB_NAME);
//            System.out.println("auctionsJsonArr = " + auctionsJsonArr);
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
        try {
            Collections.sort(newAuctions, new Comparator<Auction>() {
                @Override
                public int compare(Auction lhs, Auction rhs) {
                    return lhs.getStatus().compareToIgnoreCase(rhs.getStatus());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        auctionRecyclerAdapter = new AuctionRecyclerAdapter(this, newAuctions);
//        ((CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout)).setTitle("Screen Title");
        RecyclerView rv = (RecyclerView) findViewById(R.id.list_auctions);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(auctionRecyclerAdapter);


//        listAuctionsLv.setAdapter(auctionsAdapter);
//        listAuctionsLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent showItemIntent = new Intent(getApplicationContext(), AuctionView.class);
//                Auction auctionToView = auctionsAdapter.getItem(position);
//                showItemIntent.putExtra("auctionToView", auctionToView);
//                showItemIntent.putExtra("history", auctionToView.getHistoryString());
//
//                System.out.println("auctionToView.getHistoryString() = " + auctionToView.getHistoryString());
//                startActivity(showItemIntent);
//            }
//        });

    }

    private void initViewObject() {
        toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerVisible(Gravity.LEFT)) {
                    return;
                } else {
                    drawerLayout.openDrawer(Gravity.LEFT);
                }
            }
        });


        buyItNowTv = (TextView) findViewById(R.id.buy_it_now_found);
        titleTv = (TextView) findViewById(R.id.title_found);
        priceTv = (TextView) findViewById(R.id.price_found);
        listAuctionsLv = (RecyclerView) findViewById(R.id.list_auctions);
        picAuctionIv = (ImageView) findViewById(R.id.picture_found);
        infoLayout = (RelativeLayout) findViewById(R.id.info);
        saveBtnTv = (TextView) findViewById(R.id.save_auction_btn_tv);
        clearBtnTv = (TextView) findViewById(R.id.clear_view_btn_tv);

        drawerStringList = getResources().getStringArray(R.array.planets_array);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

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
                fileWorker.writeJsonFile(this, auctionsJsonArr.toString(), Constants.JSON_DB_NAME);
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
        if (id == R.id.paste_toolbar) {
            pasteAction();
            return true;
        }
        if (id == R.id.update_toolbar) {
            background();
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

    private void background() {

        Intent bgIntent = new Intent(this, BackgroundService.class);
        bgIntent.putExtra("auctionDB", auctionsJsonArr.toString());
//        System.out.println("auctionsJsonArr = " + auctionsJsonArr);
        PendingIntent alarmIntent = PendingIntent.getService(this, 0, bgIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), 60 * 1000, alarmIntent);
        System.out.println("SystemClock.elapsedRealtime() = " + SystemClock.elapsedRealtime());

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

        pasteAction();

    }

    public void clickFAB(View view) {

        pasteAction();

    }

    private void pasteAction() {

        saveBtnTv.setVisibility(View.INVISIBLE);
        clearBtnTv.setVisibility(View.INVISIBLE);
        analyzeClipUrl();

    }

    private void analyzeClipUrl() {
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

        if (clipboardManager.hasPrimaryClip()) {
            ClipData.Item clpItem = clipboardManager.getPrimaryClip().getItemAt(0);
            if (clpItem != null) {
                String urlToCheck = clpItem.getText().toString();
//                urlTv.setText(urlToCheck);
                itemId = getAuctionId(urlToCheck);
                foundLayout = (LinearLayout) findViewById(R.id.found_layout);
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
                itemJson.put(Constants.CREATE_AT, getNowDate());

                JSONArray historyArr = new JSONArray();
                JSONObject historyObj = new JSONObject();
                historyObj.put(Constants.HISTORY_DATE, getNowDate());
                if (foundAuction.getBuyItNow() != null) {
                    Number priceFormat = NumberFormat.getInstance().parse(foundAuction.getPrice());
                    Number buyItNowFormat = NumberFormat.getInstance().parse(foundAuction.getBuyItNow());
                    historyObj.put(Constants.HISTORY_BID, priceFormat.floatValue());
                    historyObj.put(Constants.HISTORY_BUY_IT_NOW, buyItNowFormat.floatValue());
                } else {
                    Number priceFormat = NumberFormat.getInstance().parse(foundAuction.getPrice());
                    historyObj.put(Constants.HISTORY_PRICE, priceFormat.floatValue());
                }
                historyArr.put(historyObj);
                itemJson.put(Constants.HISTORY, historyArr);
                auctionsJsonArr.put(itemJson);


                Log.d("jk saveItem", auctionsJsonArr.toString());

                fileWorker.writeJsonFile(this, auctionsJsonArr.toString(), Constants.JSON_DB_NAME);
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
        buyItNowTv.setText("");
        priceTv.setText("");
//        urlTv.setText("");
//        urlTv.setHint("Paste auction link.");
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



        @Override
        protected Void doInBackground(Integer... params) {

            try {
                Integer auctionNum = params[0];
                URL urlToCheck = new URL(auctionsJsonArr.getJSONObject(auctionNum).get(Constants.URL_JSON).toString());
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

            String infoPrice, infoBuyNow, infoBuyItNow;
            String infoTitle = auctionAnalyzeEbay.getTitle();
            final String infoUrl = auctionAnalyzeEbay.getUrl();

            //Ebay
            String buyItNowEbay = auctionAnalyzeEbay.getBuyItNow() != null ? auctionAnalyzeEbay.getBuyItNow() : "";
            String priceEbay = auctionAnalyzeEbay.getPrice();
            String statusEbay = auctionAnalyzeEbay.getStatus();
            String endTimeEbay = auctionAnalyzeEbay.getEndTime();

            //App
            String buyNowApp = auctionAnalyzeApp.getBuyItNow() != null ? auctionAnalyzeApp.getBuyItNow() : "";
            String priceApp = auctionAnalyzeApp.getPrice();
            String idApp = auctionAnalyzeApp.getItemId();

            layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            infoItem = layoutInflater.inflate(R.layout.info_item, null);
            infoItemEnd = layoutInflater.inflate(R.layout.info_item_end, null);
            TextView titleInfoItemTv = (TextView) infoItem.findViewById(R.id.info_auction_title_tv);
            TextView buyItNowInfoItemTv = (TextView) infoItem.findViewById(R.id.info_auction_buy_it_now_tv);
            TextView priceInfoItemTv = (TextView) infoItem.findViewById(R.id.info_auction_price_tv);
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

                    if (priceEbay.equals(priceApp))
                        infoBuyItNow = "BuyItNow: No changes";
                    else {
                        infoBuyItNow = "New buyItNow: " + priceEbay + " (old: " + priceApp + ")";
                        AuctionWorker.updateAuctionPrice(getApplicationContext(), auctionsJsonArr, idApp, decimalFormat.parse(priceEbay).floatValue(), Constants.NO_BUY_IT);
                    }
                    priceInfoItemTv.setVisibility(View.GONE);
                    buyItNowInfoItemTv.setText(infoBuyItNow);
                } else {
                    priceInfoItemTv.setVisibility(View.VISIBLE);

                    if (!buyNowApp.equals(buyItNowEbay) && !priceApp.equals(priceEbay)) {

                        infoBuyNow = "BuyItNow: " + buyItNowEbay + " (old: " + buyNowApp + ")";
                        infoPrice = "Price: " + priceEbay + " (old: " + priceApp + ")";

                        AuctionWorker.updateAuctionPriceAndBuy(getApplicationContext(), auctionsJsonArr, idApp, decimalFormat.parse(priceEbay).floatValue(), decimalFormat.parse(buyItNowEbay).floatValue());

                    } else if (buyNowApp.equals(buyItNowEbay) && !priceApp.equals(priceEbay)) {
                        infoBuyNow = "BuyItNow: No changes";
                        infoPrice = "Price: " + priceEbay + " (old: " + priceApp + ")";
                        AuctionWorker.updateAuctionPrice(getApplicationContext(), auctionsJsonArr, idApp, decimalFormat.parse(priceEbay).floatValue(), Constants.PRICE);
                    } else if (!buyNowApp.equals(buyItNowEbay) && priceApp.equals(priceEbay)) {
                        infoPrice = "Price No changes";
                        infoBuyNow = "BuyItNow: " + buyItNowEbay + " (old: " + buyNowApp + ")";
                        AuctionWorker.updateAuctionPrice(getApplicationContext(), auctionsJsonArr, idApp, decimalFormat.parse(buyItNowEbay).floatValue(), Constants.BUY_IT_NOW);
                    } else {
                        infoPrice = "Price No changes";
                        infoBuyNow = "BuyItNow: No changes";
                    }
                    buyItNowInfoItemTv.setText(infoBuyNow);
                    priceInfoItemTv.setText(infoPrice);
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }
            AuctionWorker.updateAuctionByName(getApplicationContext(), auctionsJsonArr, idApp, statusEbay, Constants.STATUS);
            AuctionWorker.updateAuctionByName(getApplicationContext(), auctionsJsonArr, idApp, endTimeEbay, Constants.END_TIME);
            infoListLayout.addView(infoItem);
            if (auctionCount >= 0) {
                checkAuction();
            } else {
                infoListLayout.addView(infoItemEnd);
                updateBtn.setClickable(true);
            }

        }

//        protected void updateAuction(String id, String value, String name) {
//            for (int i = 0; i < auctionsJsonArr.length(); i++) {
//                try {
//                    if (auctionsJsonArr.getJSONObject(i).get(Constants.ITEM_ID).toString() == id) {
//                        switch (name) {
//                            case Constants.STATUS:
//                                auctionsJsonArr.getJSONObject(i).put(Constants.STATUS, value);
//                                fileWorker.writeJsonFile(getApplicationContext(), auctionsJsonArr.toString(), Constants.JSON_DB_NAME);
//                                break;
//                            case Constants.END_TIME:
//                                auctionsJsonArr.getJSONObject(i).put(Constants.END_TIME, value);
//                                fileWorker.writeJsonFile(getApplicationContext(), auctionsJsonArr.toString(), Constants.JSON_DB_NAME);
//                                break;
//
//
//                        }
//                        loadJsonToLv();
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }

//        protected void updateAuction(String id, Float price, Float buyItNow) {
//            for (int i = 0; i < auctionsJsonArr.length(); i++) {
//                try {
//                    if (auctionsJsonArr.getJSONObject(i).get(Constants.ITEM_ID).toString() == id) {
//                        JSONObject historyObj = new JSONObject();
//                        auctionsJsonArr.getJSONObject(i).getJSONObject(Constants.BUYITNOW).put(Constants.VALUE, buyItNow);
//                        auctionsJsonArr.getJSONObject(i).getJSONObject(Constants.PRICE).put(Constants.VALUE, price);
//                        historyObj.put(Constants.HISTORYBUYITNOW, buyItNow);
//                        historyObj.put(Constants.HISTORYBID, price);
//                        historyObj.put(Constants.HISTORYDATE, getNowDate());
//                        auctionsJsonArr.getJSONObject(i).getJSONArray(Constants.HISTORY).put(historyObj);
//                        fileWorker.writeJsonFile(getApplicationContext(), auctionsJsonArr.toString(), Constants.JSON_DB_NAME);
//                        loadJsonToLv();
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }

//        protected void updateAuction(String id, Float value, String name) {
//
//            for (int i = 0; i < auctionsJsonArr.length(); i++) {
//                try {
//                    System.out.println("value = " + value);
//                    if (auctionsJsonArr.getJSONObject(i).get(Constants.ITEM_ID).toString() == id) {
//                        JSONObject historyObj = new JSONObject();
//                        switch (name) {
//                            case Constants.BUYITNOW:
//                                auctionsJsonArr.getJSONObject(i).getJSONObject(Constants.BUYITNOW).put(Constants.VALUE, value);
//                                historyObj.put(Constants.HISTORYBUYITNOW, value);
//                                historyObj.put(Constants.HISTORYBID, auctionsJsonArr.getJSONObject(i).getJSONObject(Constants.HISTORY).get(Constants.HISTORYBID));
//                                historyObj.put(Constants.HISTORYDATE, getNowDate());
//                                auctionsJsonArr.getJSONObject(i).getJSONArray(Constants.HISTORY).put(historyObj);
//                                break;
//                            case Constants.PRICE:
//                                auctionsJsonArr.getJSONObject(i).getJSONObject(Constants.PRICE).put(Constants.VALUE, value);
//                                historyObj.put(Constants.HISTORYBUYITNOW, auctionsJsonArr.getJSONObject(i).getJSONObject(Constants.HISTORY).get(Constants.HISTORYBUYITNOW));
//                                historyObj.put(Constants.HISTORYBID, value);
//                                historyObj.put(Constants.HISTORYDATE, getNowDate());
//                                auctionsJsonArr.getJSONObject(i).getJSONArray(Constants.HISTORY).put(historyObj);
//                                break;
//                            case Constants.NO_BUY_IT:
//                                auctionsJsonArr.getJSONObject(i).getJSONObject(Constants.PRICE).put(Constants.VALUE, value);
//                                historyObj.put(Constants.HISTORYPRICE, value);
//                                historyObj.put(Constants.HISTORYDATE, getNowDate());
//                                auctionsJsonArr.getJSONObject(i).getJSONArray(Constants.HISTORY).put(historyObj);
//                                break;
//                        }
//                        fileWorker.writeJsonFile(getApplicationContext(), auctionsJsonArr.toString(), Constants.JSON_DB_NAME);
//                        loadJsonToLv();
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//
//        }

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
                if (auctionsJsonArr.getJSONObject(auctionNum).has(Constants.BUY_IT_NOW)) {
                    auctionAnalyzeApp.setBuyItNow(auctionsJsonArr.getJSONObject(auctionNum).getJSONObject(Constants.BUY_IT_NOW).get(Constants.VALUE).toString());
                }
                auctionAnalyzeApp.setPrice(auctionsJsonArr.getJSONObject(auctionNum).getJSONObject(Constants.PRICE).get(Constants.VALUE).toString());
                auctionAnalyzeApp.setItemId(auctionsJsonArr.getJSONObject(auctionNum).get(Constants.ITEM_ID).toString());
                auctionAnalyzeApp.setCurrency(auctionsJsonArr.getJSONObject(auctionNum).getJSONObject(Constants.PRICE).get(Constants.CURRENCY).toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        protected void getEbayAuctionItem(String jsonString) throws JSONException {

            auctionAnalyzeEbay = new Auction();
            JSONObject jsonObject = new JSONObject(jsonString);
            analyzeJsonItem = jsonObject.getJSONObject(Constants.ITEM);
            Iterator it = analyzeJsonItem.keys();

            while (it.hasNext()) {
                String key = (String) it.next();
                switch (key) {
                    case Constants.PRICE:
                        auctionAnalyzeEbay.setPrice(analyzeJsonItem.getJSONObject(Constants.PRICE).get(Constants.VALUE).toString());
                        auctionAnalyzeEbay.setCurrency(analyzeJsonItem.getJSONObject(Constants.PRICE).get(Constants.CURRENCY).toString());
                        break;
                    case Constants.BUY_IT_NOW:
                        auctionAnalyzeEbay.setBuyItNow(analyzeJsonItem.getJSONObject(Constants.BUY_IT_NOW).get(Constants.VALUE).toString());
                        auctionAnalyzeEbay.setCurrency(analyzeJsonItem.getJSONObject(Constants.BUY_IT_NOW).get(Constants.CURRENCY).toString());
                        break;
                    case Constants.TITLE:
                        auctionAnalyzeEbay.setTitle(analyzeJsonItem.get(Constants.TITLE).toString());
                        break;
                    case Constants.ITEM_ID:
                        auctionAnalyzeEbay.setItemId(analyzeJsonItem.get(Constants.ITEM_ID).toString());
                        break;
                    case Constants.AUCTION_URL:
                        auctionAnalyzeEbay.setUrl(analyzeJsonItem.get(Constants.AUCTION_URL).toString());
                        break;
                    case Constants.STATUS:
                        auctionAnalyzeEbay.setStatus(analyzeJsonItem.get(Constants.STATUS).toString());
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

                Uri builtUri = Uri.parse(Constants.EBAY_SHOPPING_URL).buildUpon().appendQueryParameter(Constants.QUERY_PARAM, query_type).appendQueryParameter(Constants.FORMAT_PARAM, dataTypeJson).appendQueryParameter(Constants.APPID_PARAM, Constants.APP_ID).appendQueryParameter(Constants.SITEID_PARAM, siteId).appendQueryParameter(Constants.VERSION_PARAM, version).appendQueryParameter(Constants.ITEMID_PARAM, itemId).build();
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

            if (foundAuction.getTitle() != null) titleTv.setText(foundAuction.getTitle());
            if (foundAuction.getCurrencyBuyItNow() != null)
                buyItNowTv.setText(foundAuction.getCurrencyBuyItNow());
            if (foundAuction.getCurrencyPrice() != null) priceTv.setText(foundAuction.getCurrencyPrice());
            if (foundAuction.getTitle() != null) saveBtnTv.setVisibility(View.VISIBLE);
            clearBtnTv.setVisibility(View.VISIBLE);
//            pas.setClickable(true);
        }

        protected void getAuctionItem(String jsonString) throws JSONException {

            JSONObject jsonObject = new JSONObject(jsonString);

            jItem = jsonObject.getJSONObject(Constants.ITEM);
            Iterator it = jItem.keys();

            foundAuction.setVersion(jsonObject.get(Constants.VERSION).toString());
            foundAuction.setTimestamp(jsonObject.get(Constants.TIMESTAMP).toString());
            foundAuction.setAck(jsonObject.get(Constants.ACK).toString());

            while (it.hasNext()) {
                String key = (String) it.next();

                switch (key) {
                    case Constants.PRICE:
                        foundAuction.setPrice(jItem.getJSONObject(Constants.PRICE).get(Constants.VALUE).toString());
                        foundAuction.setCurrency(jItem.getJSONObject(Constants.PRICE).get(Constants.CURRENCY).toString());
                        break;
                    case Constants.BUY_IT_NOW:
                        foundAuction.setBuyItNow(jItem.getJSONObject(Constants.BUY_IT_NOW).get(Constants.VALUE).toString());
                        foundAuction.setCurrency(jItem.getJSONObject(Constants.BUY_IT_NOW).get(Constants.CURRENCY).toString());
                        break;
                    case Constants.PICTURE_URL:
                        if (jItem.getJSONArray(Constants.PICTURE_URL).length() > 0) {
                            foundAuction.setPicture(jItem.getJSONArray(Constants.PICTURE_URL).getString(0));
                            new GetAuctionPic().execute(foundAuction.getPicture());
                        } else {
                            foundAuction.setPicture("");
                        }
                        break;
                    case Constants.TITLE:
                        foundAuction.setTitle(jItem.get(Constants.TITLE).toString());
                        break;
                    case Constants.ITEM_ID:
                        foundAuction.setItemId(jItem.get(Constants.ITEM_ID).toString());
                        break;
                    case Constants.END_TIME:
                        foundAuction.setEndTime(jItem.get(Constants.END_TIME).toString());
                        break;
                    case Constants.AUCTION_URL:
                        foundAuction.setUrl(jItem.get(Constants.AUCTION_URL).toString());
                        break;
                    case Constants.LISTING_TYPE:
                        foundAuction.setListingType(jItem.get(Constants.LISTING_TYPE).toString());
                        break;
                    case Constants.LOCATION:
                        foundAuction.setLocation(jItem.get(Constants.LOCATION).toString());
                        break;
                    case Constants.CATEGORY:
                        foundAuction.setCategory(jItem.get(Constants.CATEGORY).toString());
                        break;
                    case Constants.CATEGORY_ID:
                        foundAuction.setCategoryId(jItem.get(Constants.CATEGORY_ID).toString());
                        break;
                    case Constants.STATUS:
                        foundAuction.setStatus(jItem.get(Constants.STATUS).toString());
                        break;
                    case Constants.COUNTRY:
                        foundAuction.setLocation(jItem.get(Constants.COUNTRY).toString());
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
