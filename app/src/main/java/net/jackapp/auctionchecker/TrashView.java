package net.jackapp.auctionchecker;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by jacekkupczak on 22.05.16.
 */
public class TrashView extends AppCompatActivity {

    Intent context;
    private JsonWorker jsonWorker = new JsonWorker();
    ArrayList<Auction> trashList;
    TrashRecyclerAdapter trashRecyclerAdapter;
    ArrayList<Auction> trashAuctions;
    RecyclerView rv;

    public TrashView() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        context = getIntent();
        setContentView(R.layout.trash_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.trash_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        loadTrashList();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.remove_all_toolbar:
                removeAllFromTrash();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_trash, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTrashList();
    }

    public void loadTrashList() {
        try {
            MainActivity.auctionsJsonArr = jsonWorker.readFileToJsonArray(this, Constants.JSON_DB_NAME);
            trashList = new ArrayList<>();
            trashList.clear();
            trashAuctions = Auction.loadFromJsonArray(true);
            Collections.sort(trashAuctions, new Comparator<Auction>() {
                @Override
                public int compare(Auction lhs, Auction rhs) {
                    return lhs.getStatus().compareToIgnoreCase(rhs.getStatus());
                }
            });
            TextView infoTrashTv = (TextView) findViewById(R.id.info_trash);
            trashRecyclerAdapter = new TrashRecyclerAdapter(this, trashAuctions, infoTrashTv);
            rv = (RecyclerView) findViewById(R.id.trash_list);
            assert rv != null;
            rv.setLayoutManager(new LinearLayoutManager(this));
            rv.setAdapter(trashRecyclerAdapter);
            if (trashAuctions.isEmpty())
                infoTrashTv.setVisibility(View.VISIBLE);
            else
                infoTrashTv.setVisibility(View.GONE);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void removeAllFromTrash() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Delete forever");
        alertDialogBuilder
                .setMessage("Remove all auction from trash?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {
                        FileWorker fileWorker = new FileWorker();
                        JsonWorker jsonWorker = new JsonWorker();
                        fileWorker.writeJsonFile(getApplicationContext(), jsonWorker.removeAllTrash(), Constants.JSON_DB_NAME);
                        loadTrashList();
                    }

                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
