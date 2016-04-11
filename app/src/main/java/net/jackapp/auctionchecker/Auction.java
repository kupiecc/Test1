package net.jackapp.auctionchecker;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by jacekkupczak on 08.04.16.
 */
public class Auction implements Parcelable {

    private String itemId;
    private String version;
    private String price;
    private String bid;
    private String currency;
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

    final static String ITEM = "Item";
    final static String BID = "ConvertedCurrentPrice";
    final static String PICTURE_URL = "PictureURL";
    final static String PRICE = "ConvertedBuyItNowPrice";
    final static String TITLE = "Title";
    final static String VALUE = "Value";
    final static String CURRENCY = "CurrencyID";
    final static String ITEM_ID = "ItemID";
    final static String VERSION = "Version";
    final static String END_TIME = "EndTime";
    final static String AUCTION_URL = "ViewItemURLForNaturalSearch";
    final static String LISTING_TYPE = "ListingType";
    final static String LOCATION = "Location";
    final static String CATEGORY = "PrimaryCategoryName";
    final static String CATEGORY_ID = "PrimaryCategoryID";
    final static String STATUS = "ListingStatus";
    final static String COUNTRY = "Country";
    final static String TIMESTAMP = "Timestamp";
    final static String ACK = "Ack";


    public Auction() {

    }

    public Auction(Parcel source) {
        this.itemId = source.readString();
        this.version = source.readString();
        this.price = source.readString();
        this.bid = source.readString();
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
        dest.writeString(bid);
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

    }

    public static final Parcelable.Creator<Auction> CREATOR = new Parcelable.Creator<Auction>(){
        @Override
        public Auction createFromParcel(Parcel source) {
            return new Auction(source);
        }

        @Override
        public Auction[] newArray(int size) {
            return new Auction[0];
        }
    };

    public static ArrayList<Auction> fromJson(JSONArray auctionDBArray) {
        ArrayList<Auction> auctions = new ArrayList<Auction>();
        for (int i = 0; i < auctionDBArray.length(); i++) {
            try {
                Auction auctionFromJson = new Auction();
                if (auctionDBArray.getJSONObject(i).has(TITLE))
                    auctionFromJson.setTitle(auctionDBArray.getJSONObject(i).getString(TITLE));

                if (auctionDBArray.getJSONObject(i).has(ITEM_ID))
                    auctionFromJson.setItemId(auctionDBArray.getJSONObject(i).getString(ITEM_ID));

                if (auctionDBArray.getJSONObject(i).has(END_TIME))
                    auctionFromJson.setEndTime(auctionDBArray.getJSONObject(i).getString(END_TIME));

                if (auctionDBArray.getJSONObject(i).has(AUCTION_URL))
                    auctionFromJson.setUrl(auctionDBArray.getJSONObject(i).getString(AUCTION_URL));

                if (auctionDBArray.getJSONObject(i).has(LOCATION))
                    auctionFromJson.setLocation(auctionDBArray.getJSONObject(i).getString(LOCATION));

                if (auctionDBArray.getJSONObject(i).has(PICTURE_URL))
                    auctionFromJson.setPicture(auctionDBArray.getJSONObject(i).getJSONArray(PICTURE_URL).getString(0));

                if (auctionDBArray.getJSONObject(i).has(PRICE)) {
                    auctionFromJson.setPrice(auctionDBArray.getJSONObject(i).getJSONObject(PRICE).getString("Value"));
                    auctionFromJson.setCurrency(auctionDBArray.getJSONObject(i).getJSONObject(PRICE).getString("CurrencyID"));
                } else {
                    auctionFromJson.setPrice("-");
                }

                if (auctionDBArray.getJSONObject(i).has(BID)) {
                    auctionFromJson.setBid(auctionDBArray.getJSONObject(i).getJSONObject(BID).getString("Value"));
                    auctionFromJson.setCurrency(auctionDBArray.getJSONObject(i).getJSONObject(BID).getString("CurrencyID"));

                } else {
                    auctionFromJson.setBid("-");
                }

                if (auctionDBArray.getJSONObject(i).has(STATUS))
                    auctionFromJson.setStatus(auctionDBArray.getJSONObject(i).getString(STATUS));

                if (auctionDBArray.getJSONObject(i).has(COUNTRY))
                    auctionFromJson.setCountry(auctionDBArray.getJSONObject(i).getString(COUNTRY));
                auctions.add(auctionFromJson);
            } catch (JSONException e) {
                Log.e("jkE", e.toString());
            }
        }
        return auctions;
    }

    public static boolean auctionExist(String id, JSONArray db) {

        boolean exist = false;

        for (int i = 0; i < db.length(); i++) {
            try {
                if (db.getJSONObject(i).getString(ITEM_ID).equals(id))
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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        if (price == null){
            this.price = "-";
        }
        else{
            this.price = price;
        }
    }

    public String getBid() {
        return bid;
    }

    public void setBid(String bid) {
        if (bid == null){
            this.bid = "-";
        }
        else{
            this.bid = bid;
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


}
