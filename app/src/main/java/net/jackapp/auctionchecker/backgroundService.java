package net.jackapp.auctionchecker;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Iterator;

/**
 * Created by jacekkupczak on 02.05.16.
 */
public class BackgroundService extends IntentService {

    JSONArray jsonArrBg;
    Auction ebayAuction;

    public BackgroundService() {
        super("Background");
    }

    @Override
    protected void onHandleIntent(Intent workIntent) {

        Constants.BG_SERVICE_RUNNING = true;

        Double ebayPrice;
        Double ebayBuyItNow;
        Double dbPrice;
        Double dbBuyItNow;
        String ebayPriceString;
        String dbPriceString;

        DecimalFormat decimalFormat;
        decimalFormat = new DecimalFormat("#.00");
        decimalFormat.setMaximumFractionDigits(2);


        String jsonDBString = workIntent.getStringExtra("auctionDB");
        try {
            if (jsonDBString != null) {
                jsonArrBg = new JSONArray(jsonDBString);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < jsonArrBg.length(); i++) {
            try {
                JSONObject jsonRow = jsonArrBg.getJSONObject(i);
                URL urlToCheckBg = new URL(jsonRow.getString(Constants.URL_JSON));
                analyzeEbay(AuctionWorker.getJsonData(urlToCheckBg));

                dbPriceString = jsonRow.getJSONObject(Constants.PRICE).getString(Constants.VALUE);
                ebayPriceString = ebayAuction.getPrice();
                dbPrice = Double.parseDouble(dbPriceString);
                ebayPrice = Double.parseDouble(ebayPriceString);
                String title = jsonRow.getString(Constants.TITLE).substring(0, 20);


                if (ebayAuction.getBuyItNow() != null && jsonRow.has(Constants.BUY_IT_NOW)) {
                    ebayBuyItNow = Double.parseDouble(ebayAuction.getBuyItNow());
                    dbBuyItNow = Double.parseDouble(jsonRow.getJSONObject(Constants.BUY_IT_NOW).getString(Constants.VALUE));
                    if (!ebayBuyItNow.equals(dbBuyItNow) || !ebayPrice.equals(dbPrice)) {
                        System.out.println(" + with buyItNow " + title);
                        AuctionWorker.updateAuctionPriceAndBuy(getApplicationContext(), jsonArrBg, ebayAuction.getItemId(), ebayPrice, ebayBuyItNow);
                    } else {
                        System.out.println(" - with buyItNow " + title);
                    }
                }else if (!ebayPrice.equals(dbPrice)) {
                    System.out.println(" - " + title);
                    AuctionWorker.updateAuctionPrice(getApplicationContext(), jsonArrBg, ebayAuction.getItemId(), ebayPrice, Constants.PRICE);
                } else {
                    System.out.println(" + " + title);
                }

            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }
        }

        Intent localIntent = new Intent(Constants.BROADCAST_ACTION);
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);

    }

    private void analyzeEbay(String ebayString) throws JSONException {

        ebayAuction = new Auction();
        JSONObject ebayJson = new JSONObject(ebayString);
        JSONObject ebayJsonItem = ebayJson.getJSONObject(Constants.ITEM);
        Iterator it = ebayJsonItem.keys();
        while (it.hasNext()) {
            String key = (String) it.next();
            switch (key) {
                case Constants.PRICE:
                    ebayAuction.setPrice(ebayJsonItem.getJSONObject(Constants.PRICE).get(Constants.VALUE).toString());
                    ebayAuction.setCurrency(ebayJsonItem.getJSONObject(Constants.PRICE).get(Constants.CURRENCY).toString());
                    break;
                case Constants.BUY_IT_NOW:
                    ebayAuction.setBuyItNow(ebayJsonItem.getJSONObject(Constants.BUY_IT_NOW).get(Constants.VALUE).toString());
                    ebayAuction.setCurrency(ebayJsonItem.getJSONObject(Constants.BUY_IT_NOW).get(Constants.CURRENCY).toString());
                    break;
                case Constants.TITLE:
                    ebayAuction.setTitle(ebayJsonItem.get(Constants.TITLE).toString());
                    break;
                case Constants.ITEM_ID:
                    ebayAuction.setItemId(ebayJsonItem.get(Constants.ITEM_ID).toString());
                    break;
                case Constants.AUCTION_URL:
                    ebayAuction.setUrl(ebayJsonItem.get(Constants.AUCTION_URL).toString());
                    break;
                case Constants.STATUS:
                    ebayAuction.setStatus(ebayJsonItem.get(Constants.STATUS).toString());
                    break;
            }
        }


    }
}
