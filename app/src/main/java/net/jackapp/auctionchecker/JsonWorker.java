package net.jackapp.auctionchecker;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by jacekkupczak on 19.04.16.
 */
public class JsonWorker {

    private final String ITEM_ID = "ItemID";

    String dataString;
    FileWorker fileWorker = new FileWorker();

    public JsonWorker() {
    }

    public JSONArray readFileToJsonArray(Context context, String fileName) throws JSONException {
        try {
            dataString = fileWorker.readFile(context, fileName);
            if (!dataString.equals(""))
                return  new JSONArray(dataString);
            else
                return new JSONArray();
        } catch (JSONException e) {
            Log.d("function readDB", e.toString());
        }
        Log.d("readDB", dataString);

        return null;
    }

    public JSONArray removeAuctionFromJson(JSONArray db, String auctionID) {
        JSONArray newDb = new JSONArray();
        for (int i = 0; i < db.length(); i++) {
            try {
                if (db.getJSONObject(i).getString(ITEM_ID) != auctionID) {
                    newDb.put(db.getJSONObject(i));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return newDb;
    }
}