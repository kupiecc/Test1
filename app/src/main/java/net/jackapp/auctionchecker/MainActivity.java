package net.jackapp.auctionchecker;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.os.SystemClock;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
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

    ActionBarDrawerToggle drawerToggle;
    ArrayList<Auction> auctionsList;
    AuctionRecyclerAdapter auctionRecyclerAdapter;
    Auction foundAuction, auctionAnalyzeEbay, auctionAnalyzeApp;
    Bitmap picBmp;
    DrawerLayout drawerLayout;
    FileWorker fileWorker = new FileWorker();
    FrameLayout drawerFrameLayout;
    ImageView picAuctionIv, updateBtn;
    Integer auctionCount = 0, auctionCheckNum;
    public static JSONArray auctionsJsonArr;
    JSONObject countriesDBJson, jItemFromPaste, analyzeJsonItem;
    JsonWorker jsonWorker = new JsonWorker();
    LinearLayout infoListLayout, foundLayout, drawerLayoutPaste;
    ListView drawerLv;
    LayoutInflater layoutInflater;
    String auctionMsg;
    RecyclerView listAuctionsLv;
    RelativeLayout infoLayout;
    String itemId, siteExtension, countriesDBString;
    TextView buyItNowTv, titleTv, priceTv, closeInfoButtonTv, saveBtnTv, clearBtnTv, infoTitleTv, drawerPasteTv;
    Toolbar toolbar;
    View infoItem, infoItemEnd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("onCreate MainWorker");
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        initViewObject();
        loadCountriesCode();
        registerBroadcast();

//        background();

        loadJsonToLv();
    }

    private void registerBroadcast() {
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                loadJsonToLv();
            }
        };
        IntentFilter bgIntentFilter = new IntentFilter(Constants.BROADCAST_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, bgIntentFilter);
    }

    private void prepareList() {
    }

    public void loadJsonToLv() {
        try {
            MainActivity.auctionsJsonArr = jsonWorker.readFileToJsonArray(this, Constants.JSON_DB_NAME);
            auctionsList = new ArrayList<>();
            auctionsList.clear();
            ArrayList<Auction> newAuctions = Auction.loadFromJsonArray();
            Collections.sort(newAuctions, new Comparator<Auction>() {
                @Override
                public int compare(Auction lhs, Auction rhs) {
                    return lhs.getStatus().compareToIgnoreCase(rhs.getStatus());
                }
            });
            auctionRecyclerAdapter = new AuctionRecyclerAdapter(this, newAuctions);
            RecyclerView rv = (RecyclerView) findViewById(R.id.list_auctions);
            assert rv != null;
            rv.setLayoutManager(new LinearLayoutManager(this));
            rv.setAdapter(auctionRecyclerAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void initViewObject() {
        System.out.println("initViewObject");
        toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        assert toolbar != null;
        toolbar.setTitle("");
        setSupportActionBar(toolbar);


        buyItNowTv = (TextView) findViewById(R.id.buy_it_now_found);
        titleTv = (TextView) findViewById(R.id.title_found);
        priceTv = (TextView) findViewById(R.id.price_found);
        listAuctionsLv = (RecyclerView) findViewById(R.id.list_auctions);
        picAuctionIv = (ImageView) findViewById(R.id.picture_found);
        infoLayout = (RelativeLayout) findViewById(R.id.info);
        saveBtnTv = (TextView) findViewById(R.id.save_auction_btn_tv);
        clearBtnTv = (TextView) findViewById(R.id.clear_view_btn_tv);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerStringList = getResources().getStringArray(R.array.drawer_items);
        drawerLv = (ListView) findViewById(R.id.drawer_lv);

        DrawerItem[] drawerItem = new DrawerItem[2];
        drawerItem[0] = new DrawerItem(R.mipmap.ic_settings_white_24dp, "Settings");
        drawerItem[1] = new DrawerItem(R.mipmap.ic_content_paste_white_24dp, "Paste auction link");

        DrawerAdapter drawerAdapter = new DrawerAdapter(this, R.layout.drawer_list_item, drawerItem);
        drawerLv.setAdapter(drawerAdapter);
        drawerLv.setOnItemClickListener(new DrawerItemClickListener());
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                System.out.println("onDrawerClosed");
                getSupportActionBar().setTitle("on drawable close");
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                System.out.println("on drawable opened");
                getSupportActionBar().setTitle("on drawable opened");
            }
        };

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

        return super.onContextItemSelected(item);

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
//        if(drawerToggle.onOptionsItemSelected(item)){
//            System.out.println("test1");
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void background() {

        System.out.println("Background()");

        if (alarmManager != null) {
            alarmManager.cancel(alarmIntent);
        }

        Intent bgIntent = new Intent(this, BackgroundService.class);
        PendingIntent alarmIntent = PendingIntent.getService(this, 0, bgIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), 2 * 60 * 1000, alarmIntent);
        startService(bgIntent);


    }

    public static void updateList() {

    }

    private void loadCountriesCode() {
        System.out.println("LoadCountriesCode");

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

        FileCache fileCache = new FileCache(this);
        fileCache.clear();
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

        saveBtnTv.setClickable(false);
        saveBtnTv.setTextColor(getResources().getColor(R.color.colorPrimary100));

        JSONObject itemJson = new JSONObject();

        if (Auction.auctionExist(foundAuction.getItemId())) {
            Toast.makeText(this, "This auction exist.", Toast.LENGTH_LONG).show();
        } else {
            try {
                Iterator it = jItemFromPaste.keys();

                //Add all data from ebay
                while (it.hasNext()) {
                    String key = (String) it.next();
                    itemJson.put(key, jItemFromPaste.get(key));
                }
                itemJson.put("SiteExtension", foundAuction.getSiteExt());
                itemJson.put("SiteID", foundAuction.getSiteId());
                itemJson.put("UrlJson", foundAuction.getUrlJson());
                itemJson.put(Constants.CREATE_AT, getNowDate());

                JSONArray historyArr = new JSONArray();
                JSONObject historyObj = new JSONObject();
                historyObj.put(Constants.HISTORY_DATE, getNowDate());
                if (foundAuction.getBuyItNow() != null) {
                    Double priceFormat = Double.parseDouble(foundAuction.getPrice());
                    Double buyItNowFormat = Double.parseDouble(foundAuction.getBuyItNow());
                    historyObj.put(Constants.HISTORY_PRICE, priceFormat.floatValue());
                    historyObj.put(Constants.HISTORY_BUY_IT_NOW, buyItNowFormat.floatValue());
                } else {
                    Double priceFormat = Double.parseDouble(foundAuction.getPrice());
                    historyObj.put(Constants.HISTORY_PRICE, priceFormat.floatValue());
                }
                historyArr.put(historyObj);
                itemJson.put(Constants.HISTORY, historyArr);
                MainActivity.auctionsJsonArr.put(itemJson);

                fileWorker.writeJsonFile(this, MainActivity.auctionsJsonArr, Constants.JSON_DB_NAME);
                loadJsonToLv();
                clearFoundItem();
                background();

            } catch (JSONException e) {
                Log.d("jk", "saveAuction function problem: " + e.toString());
                e.printStackTrace();
            }
        }
        System.out.println("DB titles after save");
        saveBtnTv.setClickable(true);
        saveBtnTv.setTextColor(getResources().getColor(R.color.colorPrimary));

        StaticWorker.showDBTitles();
    }

    public void closeFoundItem(View view) {

        clearFoundItem();

    }

    private void clearFoundItem() {
        titleTv.setText("");
        picAuctionIv.setImageResource(R.drawable.no_img);
        buyItNowTv.setText("");
        priceTv.setText("");
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
        auctionCount = MainActivity.auctionsJsonArr.length() - 1;
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
        auctionCheckNum = MainActivity.auctionsJsonArr.length() - auctionCount;
        auctionMsg = "Check auction: " + String.valueOf(auctionCheckNum) + "/" + MainActivity.auctionsJsonArr.length();
        infoTitleTv.setText(auctionMsg);
        new AnalyzeAuctions().execute(auctionCount);
        auctionCount = auctionCount - 1;
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }

        private void selectItem(int position) {

            switch (position) {
                case 0:
                    System.out.println("Settings clicked");
                    Intent settingsIntent = new Intent(getApplicationContext(), SettingsView.class);
                    settingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getApplicationContext().startActivity(settingsIntent);
                    drawerLayout.closeDrawers();
                    break;
                case 1:
                    pasteAction();
                    drawerLayout.closeDrawers();
                    break;
                default:
                    break;
            }


        }

    }

    class AnalyzeAuctions extends AsyncTask<Integer, Void, Void> {


        @Override
        protected Void doInBackground(Integer... params) {

            try {
                Integer auctionNum = params[0];
                URL urlToCheck = new URL(MainActivity.auctionsJsonArr.getJSONObject(auctionNum).get(Constants.URL_JSON).toString());
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

            if (buyItNowEbay.equals("") || buyNowApp.equals("")) {

                if (priceEbay.equals(priceApp))
                    infoBuyItNow = "BuyItNow: No changes";
                else {
                    infoBuyItNow = "New buyItNow: " + priceEbay + " (old: " + priceApp + ")";
//                        AuctionWorker.updateAuctionPrice(getApplicationContext(), auctionsJsonArr, idApp, decimalFormat.parse(priceEbay).floatValue(), Constants.NO_BUY_IT);
                }
                priceInfoItemTv.setVisibility(View.GONE);
                buyItNowInfoItemTv.setText(infoBuyItNow);
            } else {
                priceInfoItemTv.setVisibility(View.VISIBLE);

                if (!buyNowApp.equals(buyItNowEbay) && !priceApp.equals(priceEbay)) {

                    infoBuyNow = "BuyItNow: " + buyItNowEbay + " (old: " + buyNowApp + ")";
                    infoPrice = "Price: " + priceEbay + " (old: " + priceApp + ")";

//                        AuctionWorker.updateAuctionPriceAndBuy(getApplicationContext(), auctionsJsonArr, idApp, decimalFormat.parse(priceEbay).floatValue(), decimalFormat.parse(buyItNowEbay).floatValue());

                } else if (buyNowApp.equals(buyItNowEbay) && !priceApp.equals(priceEbay)) {
                    infoBuyNow = "BuyItNow: No changes";
                    infoPrice = "Price: " + priceEbay + " (old: " + priceApp + ")";
//                        AuctionWorker.updateAuctionPrice(getApplicationContext(), auctionsJsonArr, idApp, decimalFormat.parse(priceEbay).floatValue(), Constants.PRICE);
                } else if (!buyNowApp.equals(buyItNowEbay) && priceApp.equals(priceEbay)) {
                    infoPrice = "Price No changes";
                    infoBuyNow = "BuyItNow: " + buyItNowEbay + " (old: " + buyNowApp + ")";
//                        AuctionWorker.updateAuctionPrice(getApplicationContext(), auctionsJsonArr, idApp, decimalFormat.parse(buyItNowEbay).floatValue(), Constants.BUY_IT_NOW);
                } else {
                    infoPrice = "Price No changes";
                    infoBuyNow = "BuyItNow: No changes";
                }
                buyItNowInfoItemTv.setText(infoBuyNow);
                priceInfoItemTv.setText(infoPrice);
            }

            AuctionWorker.updateAuctionByName(getApplicationContext(), idApp, statusEbay, Constants.STATUS);
            AuctionWorker.updateAuctionByName(getApplicationContext(), idApp, endTimeEbay, Constants.END_TIME);
            infoListLayout.addView(infoItem);
            if (auctionCount >= 0) {
                checkAuction();
            } else {
                infoListLayout.addView(infoItemEnd);
                updateBtn.setClickable(true);
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
                if (MainActivity.auctionsJsonArr.getJSONObject(auctionNum).has(Constants.BUY_IT_NOW)) {
                    auctionAnalyzeApp.setBuyItNow(MainActivity.auctionsJsonArr.getJSONObject(auctionNum).getJSONObject(Constants.BUY_IT_NOW).get(Constants.VALUE).toString());
                }
                auctionAnalyzeApp.setPrice(MainActivity.auctionsJsonArr.getJSONObject(auctionNum).getJSONObject(Constants.PRICE).get(Constants.VALUE).toString());
                auctionAnalyzeApp.setItemId(MainActivity.auctionsJsonArr.getJSONObject(auctionNum).get(Constants.ITEM_ID).toString());
                auctionAnalyzeApp.setCurrency(MainActivity.auctionsJsonArr.getJSONObject(auctionNum).getJSONObject(Constants.PRICE).get(Constants.CURRENCY).toString());

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
            if (foundAuction.getCurrencyPrice() != null)
                priceTv.setText(foundAuction.getCurrencyPrice());
            if (foundAuction.getTitle() != null) saveBtnTv.setVisibility(View.VISIBLE);
            clearBtnTv.setVisibility(View.VISIBLE);
            System.out.println("Auction after paste = ");
            StaticWorker.showDBTitles();
        }

        protected void getAuctionItem(String jsonString) throws JSONException {

            JSONObject jsonObject = new JSONObject(jsonString);

            jItemFromPaste = jsonObject.getJSONObject(Constants.ITEM);
            Iterator it = jItemFromPaste.keys();

            foundAuction.setVersion(jsonObject.get(Constants.VERSION).toString());
            foundAuction.setTimestamp(jsonObject.get(Constants.TIMESTAMP).toString());
            foundAuction.setAck(jsonObject.get(Constants.ACK).toString());

            while (it.hasNext()) {
                String key = (String) it.next();

                switch (key) {
                    case Constants.PRICE:
                        foundAuction.setPrice(jItemFromPaste.getJSONObject(Constants.PRICE).get(Constants.VALUE).toString());
                        foundAuction.setCurrency(jItemFromPaste.getJSONObject(Constants.PRICE).get(Constants.CURRENCY).toString());
                        break;
                    case Constants.BUY_IT_NOW:
                        foundAuction.setBuyItNow(jItemFromPaste.getJSONObject(Constants.BUY_IT_NOW).get(Constants.VALUE).toString());
                        foundAuction.setCurrency(jItemFromPaste.getJSONObject(Constants.BUY_IT_NOW).get(Constants.CURRENCY).toString());
                        break;
                    case Constants.PICTURE_URL:
                        if (jItemFromPaste.getJSONArray(Constants.PICTURE_URL).length() > 0) {
                            foundAuction.setPicture(jItemFromPaste.getJSONArray(Constants.PICTURE_URL).getString(0));
                            new GetAuctionPic().execute(foundAuction.getPicture());
                        } else {
                            foundAuction.setPicture("");
                        }
                        break;
                    case Constants.TITLE:
                        foundAuction.setTitle(jItemFromPaste.get(Constants.TITLE).toString());
                        break;
                    case Constants.ITEM_ID:
                        foundAuction.setItemId(jItemFromPaste.get(Constants.ITEM_ID).toString());
                        break;
                    case Constants.END_TIME:
                        foundAuction.setEndTime(jItemFromPaste.get(Constants.END_TIME).toString());
                        break;
                    case Constants.AUCTION_URL:
                        foundAuction.setUrl(jItemFromPaste.get(Constants.AUCTION_URL).toString());
                        break;
                    case Constants.LISTING_TYPE:
                        foundAuction.setListingType(jItemFromPaste.get(Constants.LISTING_TYPE).toString());
                        break;
                    case Constants.LOCATION:
                        foundAuction.setLocation(jItemFromPaste.get(Constants.LOCATION).toString());
                        break;
                    case Constants.CATEGORY:
                        foundAuction.setCategory(jItemFromPaste.get(Constants.CATEGORY).toString());
                        break;
                    case Constants.CATEGORY_ID:
                        foundAuction.setCategoryId(jItemFromPaste.get(Constants.CATEGORY_ID).toString());
                        break;
                    case Constants.STATUS:
                        foundAuction.setStatus(jItemFromPaste.get(Constants.STATUS).toString());
                        break;
                    case Constants.COUNTRY:
                        foundAuction.setLocation(jItemFromPaste.get(Constants.COUNTRY).toString());
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
