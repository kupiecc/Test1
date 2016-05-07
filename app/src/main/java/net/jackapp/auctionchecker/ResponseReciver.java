package net.jackapp.auctionchecker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by jacekkupczak on 02.05.16.
 */
public class ResponseReciver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        System.out.println("ResponseReciver.class.toString() = " + ResponseReciver.class.toString());

    }
}
