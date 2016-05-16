package net.jackapp.auctionchecker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by jacekkupczak on 11.05.16.
 */
public class SettingsView extends Activity {

    Intent context;

    public SettingsView() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getIntent();

        setContentView(R.layout.settings_view);

    }
}
