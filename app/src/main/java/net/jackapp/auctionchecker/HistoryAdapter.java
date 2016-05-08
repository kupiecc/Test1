package net.jackapp.auctionchecker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

/**
 * Created by jacekkupczak on 23.04.16.
 */
public class HistoryAdapter extends BaseAdapter implements ListAdapter {

    private final Context context;
    private final JSONArray historyArray;
    private final String currency;

    public HistoryAdapter(Context context, JSONArray historyArray, String currency) {

        assert context != null;
        assert historyArray != null;

        this.context = context;
        this.historyArray = historyArray;
        this.currency = currency;

    }

    @Override
    public int getCount() {
        return historyArray.length();
    }

    @Override
    public Object getItem(int position) {
        return historyArray.optJSONObject(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.history_row, null);
        }

        String date;
        String price;
        String buyItNow;

        TextView dateTv = (TextView) convertView.findViewById(R.id.history_row_date);
        TextView priceTv = (TextView) convertView.findViewById(R.id.history_row_price);
        TextView buyItNowTv = (TextView) convertView.findViewById(R.id.history_row_buy_it_now);


        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        DecimalFormat decimalFormat = new DecimalFormat("###,###.00",  symbols);
        decimalFormat.setMaximumFractionDigits(2);

        try{
            JSONObject historyObj = historyArray.getJSONObject(position);
            date = historyObj.get("Date").toString();
            dateTv.setText(date);
            price = historyObj.get("Price").toString();
            price = decimalFormat.format(Double.parseDouble(price));
            price = "Price: " + price + currency;
            priceTv.setText(price);
            if(historyObj.has("BuyItNow")) {
                buyItNow = historyObj.get("BuyItNow").toString();
                buyItNow = decimalFormat.format(Float.parseFloat(buyItNow));
                buyItNow = "Buy now: " + buyItNow + currency;
                buyItNowTv.setText(buyItNow);
                buyItNowTv.setVisibility(View.VISIBLE);
            }else {
                buyItNowTv.setVisibility(View.GONE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return convertView;
    }
}
