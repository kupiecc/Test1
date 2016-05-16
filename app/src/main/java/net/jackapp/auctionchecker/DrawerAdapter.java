package net.jackapp.auctionchecker;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by jacekkupczak on 11.05.16.
 */
public class DrawerAdapter extends ArrayAdapter<DrawerItem> {
    Context context;
    int layoutResourceId;
    DrawerItem data[] = null;

    public DrawerAdapter(Context context, int layoutResourceId, DrawerItem[] data) {
        super(context, layoutResourceId, data);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View listItem = convertView;

        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        listItem = inflater.inflate(layoutResourceId, parent, false);

        ImageView drawerIcon = (ImageView) listItem.findViewById(R.id.drawer_img);
        TextView drawerTv = (TextView) listItem.findViewById(R.id.drawer_tv);

        DrawerItem folder = data[position];
        drawerIcon.setImageResource(folder.icon);
        drawerTv.setText(folder.title);


        return listItem;
    }
}
