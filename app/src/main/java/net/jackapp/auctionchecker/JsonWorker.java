package net.jackapp.auctionchecker;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by jacekkupczak on 19.04.16.
 */
public class JsonWorker {

    String dataString;
    FileWorker fileWorker;

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
}
