package net.jackapp.auctionchecker;

import org.json.JSONException;

/**
 * Created by jacekkupczak on 10.05.16.
 */
public class StaticWorker {

    public static void showDBTitles () {

        for (int i = 0; i < MainActivity.auctionsJsonArr.length(); i++) {
            try {
                System.out.println("Db titles = " + MainActivity.auctionsJsonArr.getJSONObject(i).getString(Constants.TITLE)
                        + " img = "  + MainActivity.auctionsJsonArr.getJSONObject(i).getString(Constants.PICTURE_URL)
                );
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    public static void sortDb(){

    }

}
