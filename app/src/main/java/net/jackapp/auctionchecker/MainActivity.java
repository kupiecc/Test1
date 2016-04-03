package net.jackapp.auctionchecker;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

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

        String linkToCheck = "http://open.api.ebay.com/shopping?callname=GetSingleItem&responseencoding=XML&appid=Individu-PriceChe-PRD-53a0284bf-5b5a8f9b&siteid=0&version=515&ItemID=111942318507";

        assert foundAuctionLbl != null;
        foundAuctionLbl.setText(linkToCheck);

        new GetAuctionJSON().execute();

    }

    class GetAuctionJSON extends AsyncTask<Void, Void, Void>{

        String jsonString = "";
        String result = "";

        @Override
        protected Void doInBackground(Void... params) {

            TextView foundAuctionLink = (TextView) findViewById(R.id.found_auction_lbl);

            try {
                URL urlToCheck = new URL("http://open.api.ebay.com/shopping?callname=GetSingleItem&responseencoding=XML&appid=Individu-PriceChe-PRD-53a0284bf-5b5a8f9b&siteid=0&version=515&ItemID=111942318507");

                URLConnection urlConnection = urlToCheck.openConnection();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            TextView result = (TextView) findViewById(R.id.result);
            TextView foundAuctionLbl = (TextView) findViewById(R.id.found_auction_lbl);

            foundAuctionLbl.setText("gotowe");

            result.setText(stringToPrint);

        }
    }
}
