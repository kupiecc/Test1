package net.jackapp.auctionchecker;

import android.content.Context;

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
import java.util.Calendar;

/**
 * Created by jacekkupczak on 05.05.16.
 */
public class AuctionWorker {

    private static FileWorker fileWorker = new FileWorker();

    public static String getJsonData(URL url) throws IOException {
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

    private static String getNowDate() {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd, HH:mm");
        String date = df.format(Calendar.getInstance().getTime());
        return date;
    }


    public static void updateAuctionByName(Context context, JSONArray jsonArray, String id, String value, String name) {
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                if (jsonArray.getJSONObject(i).get(Constants.ITEM_ID).toString() == id) {
                    switch (name) {
                        case Constants.STATUS:
                            jsonArray.getJSONObject(i).put(Constants.STATUS, value);
                            jsonArray.getJSONObject(i).put(Constants.UPDATED_AT, getNowDate());
                            fileWorker.writeJsonFile(context, jsonArray.toString(), Constants.JSON_DB_NAME);
                            break;
                        case Constants.END_TIME:
                            jsonArray.getJSONObject(i).put(Constants.END_TIME, value);
                            jsonArray.getJSONObject(i).put(Constants.UPDATED_AT, getNowDate());
                            fileWorker.writeJsonFile(context, jsonArray.toString(), Constants.JSON_DB_NAME);
                            break;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public static void updateAuctionPriceAndBuy(Context context, JSONArray jsonArray, String id, Double price, Double buyItNow) {
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                if (jsonArray.getJSONObject(i).get(Constants.ITEM_ID).toString().equals(id)) {


                    JSONObject historyObj = new JSONObject();
                    historyObj.put(Constants.HISTORY_PRICE, price);
                    historyObj.put(Constants.HISTORY_DATE, getNowDate());
                    historyObj.put(Constants.HISTORY_BUY_IT_NOW, buyItNow);

                    jsonArray.getJSONObject(i).getJSONArray(Constants.HISTORY).put(historyObj);
                    jsonArray.getJSONObject(i).put(Constants.UPDATED_AT, getNowDate());
                    jsonArray.getJSONObject(i).getJSONObject(Constants.BUY_IT_NOW).put(Constants.VALUE, buyItNow);
                    jsonArray.getJSONObject(i).getJSONObject(Constants.PRICE).put(Constants.VALUE, price);
                    fileWorker.writeJsonFile(context, jsonArray.toString(), Constants.JSON_DB_NAME);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public static void updateAuctionPrice(Context context, JSONArray jsonArray, String id, Double value, String name) {

        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                if (jsonArray.getJSONObject(i).get(Constants.ITEM_ID).toString().equals(id)) {
                    System.out.println("updateAuctionPrice itemId = " + jsonArray.getJSONObject(i).get(Constants.ITEM_ID) + "; id = " + id + "; new value = " + value + "; name: " + name);
                    JSONObject historyObj = new JSONObject();
                    switch (name) {
                        case Constants.BUY_IT_NOW:
                            jsonArray.getJSONObject(i).getJSONObject(Constants.BUY_IT_NOW).put(Constants.VALUE, value);
                            historyObj.put(Constants.HISTORY_BUY_IT_NOW, value);
                            historyObj.put(Constants.HISTORY_DATE, getNowDate());
                            jsonArray.getJSONObject(i).getJSONArray(Constants.HISTORY).put(historyObj);
                            break;
                        case Constants.PRICE:
                            jsonArray.getJSONObject(i).getJSONObject(Constants.PRICE).put(Constants.VALUE, value);
                            historyObj.put(Constants.HISTORY_PRICE, value);
                            historyObj.put(Constants.HISTORY_DATE, getNowDate());
                            jsonArray.getJSONObject(i).getJSONArray(Constants.HISTORY).put(historyObj);
                            break;
                    }
                    jsonArray.getJSONObject(i).put(Constants.UPDATED_AT, getNowDate());
                    fileWorker.writeJsonFile(context, jsonArray.toString(), Constants.JSON_DB_NAME);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

}
