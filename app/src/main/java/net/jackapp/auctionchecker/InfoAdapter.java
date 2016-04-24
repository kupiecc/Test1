package net.jackapp.auctionchecker;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

/**
 * Created by jacekkupczak on 24.04.16.
 */
public class InfoAdapter extends ArrayAdapter implements ListAdapter {


    public InfoAdapter(Context context, int resource) {
        super(context, resource);
    }
}
