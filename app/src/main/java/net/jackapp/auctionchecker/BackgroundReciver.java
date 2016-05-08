package net.jackapp.auctionchecker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by jacekkupczak on 08.05.16.
 */
public class BackgroundReciver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("intent.getExtras() = ");

    }
}
