package net.jackapp.auctionchecker;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;

/**
 * Created by jacekkupczak on 08.04.16.
 */
public class Auction implements Parcelable {

    private String itemId;
    private String version;
    private JSONArray history;
    private String historyString;
    private String historyPrice;
    private String historyBuyItNow;
    private String historyDate;
    private String price;
    private String buyItNow;
    private String currency;
    private String endDateTime;
    private String endDate;
    private String endTime;
    private String url;
    private String listingType;
    private String location;
    private String pictureUrl;
    private String category;
    private String categoryId;
    private String status;
    private String title;
    private String country;
    private String ack;
    private String timestamp;
    private String siteId;
    private String siteExt;
    private String urlJson;
    private String createAt;

    DecimalFormat currencyFormat;
    DecimalFormat decimalFormat;
    DecimalFormatSymbols symbols;

    public Auction() {
        currencyFormat = new DecimalFormat("###,###.00");
        currencyFormat.setMaximumFractionDigits(2);
        decimalFormat = new DecimalFormat("###.00");
        decimalFormat.setMaximumFractionDigits(2);
        symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
    }

    public Auction(Parcel source) {
        this.itemId = source.readString();
        this.version = source.readString();
        this.price = source.readString();
        this.buyItNow = source.readString();
        this.currency = source.readString();
        this.endTime = source.readString();
        this.url = source.readString();
        this.listingType = source.readString();
        this.location = source.readString();
        this.pictureUrl = source.readString();
        this.category = source.readString();
        this.categoryId = source.readString();
        this.status = source.readString();
        this.title = source.readString();
        this.country = source.readString();
        this.ack = source.readString();
        this.timestamp = source.readString();
        this.createAt = source.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(itemId);
        dest.writeString(version);
        dest.writeString(price);
        dest.writeString(buyItNow);
        dest.writeString(currency);
        dest.writeString(endTime);
        dest.writeString(url);
        dest.writeString(listingType);
        dest.writeString(location);
        dest.writeString(pictureUrl);
        dest.writeString(category);
        dest.writeString(categoryId);
        dest.writeString(status);
        dest.writeString(title);
        dest.writeString(country);
        dest.writeString(ack);
        dest.writeString(timestamp);
        dest.writeString(createAt);

    }

    public static final Parcelable.Creator<Auction> CREATOR = new Parcelable.Creator<Auction>() {
        @Override
        public Auction createFromParcel(Parcel source) {
            return new Auction(source);
        }

        @Override
        public Auction[] newArray(int size) {
            return new Auction[0];
        }
    };

    public static ArrayList<Auction> loadFromJsonArray(JSONArray auctionDBArray) {
        ArrayList<Auction> auctions = new ArrayList<Auction>();
        for (int i = 0; i < auctionDBArray.length(); i++) {
            try {
                Auction auctionFromJson = new Auction();
                if (auctionDBArray.getJSONObject(i).has(Constants.TITLE))
                    auctionFromJson.setTitle(auctionDBArray.getJSONObject(i).getString(Constants.TITLE));

                if (auctionDBArray.getJSONObject(i).has(Constants.ITEM_ID))
                    auctionFromJson.setItemId(auctionDBArray.getJSONObject(i).getString(Constants.ITEM_ID));

                if (auctionDBArray.getJSONObject(i).has(Constants.END_TIME))
                    auctionFromJson.setEndTime(auctionDBArray.getJSONObject(i).getString(Constants.END_TIME));

                if (auctionDBArray.getJSONObject(i).has(Constants.AUCTION_URL))
                    auctionFromJson.setUrl(auctionDBArray.getJSONObject(i).getString(Constants.AUCTION_URL));

                if (auctionDBArray.getJSONObject(i).has(Constants.LOCATION))
                    auctionFromJson.setLocation(auctionDBArray.getJSONObject(i).getString(Constants.LOCATION));

                if (auctionDBArray.getJSONObject(i).has(Constants.PICTURE_URL)) {
//                    System.out.println("auctionDBArray = " + auctionDBArray.getJSONObject(i).get(PICTURE_URL));
                    if (auctionDBArray.getJSONObject(i).getJSONArray(Constants.PICTURE_URL).length() > 0) {
                        auctionFromJson.setPicture(auctionDBArray.getJSONObject(i).getJSONArray(Constants.PICTURE_URL).getString(0));
                    } else {
                        auctionFromJson.setPicture("");
                    }
                }

                if (auctionDBArray.getJSONObject(i).has(Constants.HISTORY)) {
                    JSONArray historyObjArr = auctionDBArray.getJSONObject(i).getJSONArray(Constants.HISTORY);
                    auctionFromJson.setHistory(historyObjArr);
                    auctionFromJson.setHistoryString(historyObjArr);
                }

                if (auctionDBArray.getJSONObject(i).has(Constants.BUY_IT_NOW)) {
                    auctionFromJson.setBuyItNow(auctionDBArray.getJSONObject(i).getJSONObject(Constants.BUY_IT_NOW).getString("Value"));
                    auctionFromJson.setCurrency(auctionDBArray.getJSONObject(i).getJSONObject(Constants.BUY_IT_NOW).getString("CurrencyID"));
                } else {
                    auctionFromJson.setBuyItNow("-");
                }

                if (auctionDBArray.getJSONObject(i).has(Constants.PRICE)) {
                    auctionFromJson.setPrice(auctionDBArray.getJSONObject(i).getJSONObject(Constants.PRICE).getString("Value"));
                    auctionFromJson.setCurrency(auctionDBArray.getJSONObject(i).getJSONObject(Constants.PRICE).getString("CurrencyID"));

                } else {
                    auctionFromJson.setPrice("-");
                }

                if (auctionDBArray.getJSONObject(i).has(Constants.CREATE_AT))
                    auctionFromJson.setStatus(auctionDBArray.getJSONObject(i).getString(Constants.CREATE_AT));

                if (auctionDBArray.getJSONObject(i).has(Constants.STATUS))
                    auctionFromJson.setStatus(auctionDBArray.getJSONObject(i).getString(Constants.STATUS));

                if (auctionDBArray.getJSONObject(i).has(Constants.COUNTRY))
                    auctionFromJson.setCountry(auctionDBArray.getJSONObject(i).getString(Constants.COUNTRY));
                auctions.add(auctionFromJson);
            } catch (JSONException e) {
                Log.e("Auction fromJson()", e.toString());
            }
        }
        return auctions;
    }

    public static boolean auctionExist(String id, JSONArray db) {

        boolean exist = false;

        for (int i = 0; i < db.length(); i++) {
            try {
                if (db.getJSONObject(i).getString(Constants.ITEM_ID).equals(id))
                    exist = true;
            } catch (JSONException e) {
                Log.e("function auctionExist", e.toString());
            }
        }

        return exist;

    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public JSONArray getHistory() {
        return history;
    }

    public void setHistory(JSONArray history) {
        this.history = history;
    }


    public String getHistoryString() {
        return historyString;
    }

    public void setHistoryString(JSONArray history) {
        this.historyString = history.toString();
    }


    public String getBuyItNow() {
        try {
            if (buyItNow != null && buyItNow != "-") {
                Float buyItNowF = Float.parseFloat(buyItNow);
                decimalFormat = new DecimalFormat("###.00");
                decimalFormat.setMaximumFractionDigits(2);
                decimalFormat.setDecimalFormatSymbols(symbols);
                String newbuyItNow = decimalFormat.format(buyItNowF);
                return newbuyItNow;
            }
        } catch (NumberFormatException e) {
            Log.e("getbuyItNow NFE", e.toString());
            return buyItNow;
        }
        return buyItNow;
    }

    public String getCurrencyBuyItNow() {
        try {
            if (buyItNow != null && buyItNow != "-") {
                Float buyItNowF = Float.valueOf(buyItNow);
                currencyFormat = new DecimalFormat("###,###.00");
                currencyFormat.setMaximumFractionDigits(2);
                currencyFormat.setDecimalFormatSymbols(symbols);
                String buyItNowCurr = currencyFormat.format(buyItNowF);
                return buyItNowCurr + " " + getCurrency();
            }
        } catch (NumberFormatException e) {
            Log.e("getbuyItNow", e.toString());
        }
        return buyItNow;
    }

    public void setBuyItNow(String buyItNow) {
        if (buyItNow == null) {
            this.buyItNow = "-";
        } else {
            this.buyItNow = buyItNow;
        }
    }

    public String getPrice() {
        try {
            if (price != null && price != "-") {
                Float priceF = Float.parseFloat(price);

                decimalFormat = new DecimalFormat("###.00");
                decimalFormat.setMaximumFractionDigits(2);
                decimalFormat.setDecimalFormatSymbols(symbols);
                String newPrice = decimalFormat.format(priceF);
                return newPrice;
            }
        } catch (NumberFormatException e) {
            Log.e("getPrice NFE", e.toString());
            return price;
        }
        return price;
    }

    public String getCurrencyPrice() {
        try {
            if (price != null && price != "-") {
                Float priceF = Float.valueOf(price);
                currencyFormat = new DecimalFormat("###,###.00");
                currencyFormat.setMaximumFractionDigits(2);
                currencyFormat.setDecimalFormatSymbols(symbols);
                String priceCurr = currencyFormat.format(priceF);
                return priceCurr + " " + getCurrency();
            }
        } catch (NumberFormatException e) {
            Log.e("getPrice", e.toString());
        }
        return price;
    }

    public void setPrice(String price) {
        if (price == null) {
            this.price = "-";
        } else {
            this.price = price;
        }
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getEndDateTime() {
        String dateString = "";
        if(getEndTime() != null){
            String fullDate = getEndTime();
            String dateTime = fullDate.substring(0, fullDate.length() - 5);
            String[] dateTimeArr = dateTime.split("T");
            dateString = dateTimeArr[0] + " " + dateTimeArr[1];
        }else {
            dateString = "-";
        }
//        System.out.println("dateString = " + fullDate);
        return dateString;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getListingType() {
        return listingType;
    }

    public void setListingType(String listingType) {
        this.listingType = listingType;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPicture() {
        return pictureUrl;
    }

    public void setPicture(String pictureUrl) {

        this.pictureUrl = pictureUrl.replace("\\", "");
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getAck() {
        return ack;
    }

    public void setAck(String ack) {
        this.ack = ack;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public String getSiteExt() {
        return siteExt;
    }

    public void setSiteExt(String siteExt) {
        this.siteExt = siteExt;
    }

    public String getUrlJson() {
        return urlJson;
    }

    public void setUrlJson(String urlJson) {
        this.urlJson = urlJson;
    }

}
