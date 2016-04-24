package net.jackapp.auctionchecker;

import android.app.Dialog;
import android.content.Context;
import android.widget.TextView;


/**
 * Created by jacekkupczak on 17.04.16.
 */
public class MsgWorker extends Dialog {



    public MsgWorker(Context context, String msg) {
        super(context);

        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.info);
        dialog.setTitle("Checking...");
        TextView infoTv = (TextView) findViewById(R.id.info_auction_title_tv);
        infoTv.setText(msg);
        dialog.show();

    }

}
