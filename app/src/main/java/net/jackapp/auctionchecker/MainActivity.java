package net.jackapp.auctionchecker;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

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

public class MainActivity extends AppCompatActivity {

    TextView foundAuctionLbl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

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

        TextView foundAuctionLbl = (TextView) findViewById(R.id.found_auction_lbl);
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

        ClipData.Item clpItem = clipboardManager.getPrimaryClip().getItemAt(0);
        String linkToCheck = clpItem.getText().toString();

        assert foundAuctionLbl != null;
        foundAuctionLbl.setText(linkToCheck);

        new GetAuctionJSON().execute();

    }

    class GetAuctionJSON extends AsyncTask<Void, Void, Void>{

        String jsonString = "";
        String result = "";
        String auctionPrice = "";
        String auctionCurrencyID = "";

        @Override
        protected Void doInBackground(Void... params) {

            TextView foundAuctionLink = (TextView) findViewById(R.id.found_auction_lbl);

            try {

                URL urlToCheck = new URL("http://open.api.ebay.com/shopping?callname=GetSingleItem&responseencoding=JSON&appid=Individu-PriceChe-PRD-53a0284bf-5b5a8f9b&siteid=0&version=515&ItemID=111942318507");
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

            TextView resultTV = (TextView) findViewById(R.id.result);
            TextView foundAuctionLbl = (TextView) findViewById(R.id.found_auction_lbl);

            assert foundAuctionLbl != null;
            foundAuctionLbl.setText("gotowe");
            assert resultTV != null;
            resultTV.setText(result);
        }

        protected void getAuctionItem(JSONObject jsonObject) throws JSONException {

            JSONObject jItem = jsonObject.getJSONObject("Item");
            JSONObject jPrice = jItem.getJSONObject("ConvertedBuyItNowPrice");
            auctionPrice = jPrice.get("Value").toString();
            auctionCurrencyID = jPrice.get("CurrencyID").toString();
            result = "Cena aukcji: " + auctionPrice + auctionCurrencyID;
            Log.d("jk", result);
        }

    }
}
