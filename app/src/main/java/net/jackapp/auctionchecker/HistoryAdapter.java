package net.jackapp.auctionchecker;

import android.content.Context;
import android.text.format.DateFormat;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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

        String dateString;
        String price;
        String buyItNow;

        String time;
        String day;
        String month;
        String year;

        TextView dayTv = (TextView) convertView.findViewById(R.id.history_row_day);
        TextView monthTv = (TextView) convertView.findViewById(R.id.history_row_month);
        TextView yearTv = (TextView) convertView.findViewById(R.id.history_row_year);
        TextView timeTv = (TextView) convertView.findViewById(R.id.history_row_time);
        TextView priceTv = (TextView) convertView.findViewById(R.id.history_row_price);
        TextView buyItNowTv = (TextView) convertView.findViewById(R.id.history_row_buy_it_now);


        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        DecimalFormat decimalFormat = new DecimalFormat("###,###.00",  symbols);
        decimalFormat.setMaximumFractionDigits(2);

        try{
            JSONObject historyObj = historyArray.getJSONObject(position);
            dateString = historyObj.get("Date").toString();

            SimpleDateFormat dateToParse = new SimpleDateFormat("yyyy-MM-dd, HH:mm");
            Date parseDate = dateToParse.parse(dateString);
            month = (String) DateFormat.format("MMM", parseDate);
            day = (String) DateFormat.format("dd", parseDate);
            year = (String) DateFormat.format("yyyy", parseDate);
            time = (String) DateFormat.format("HH:mm", parseDate);
            monthTv.setText(month);
            yearTv.setText(year);
            dayTv.setText(day);
            timeTv.setText(time);

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
        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }


        return convertView;
    }
}
