package net.jackapp.auctionchecker;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by jacekkupczak on 08.04.16.
 */
public class Auction {

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
    final static String BID = "ConvertedBuyItNowPrice";
    final static String PICTURE_URL = "PictureURL";
    final static String PRICE = "ConvertedCurrentPrice";
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
                    auctionFromJson.setPicture(auctionDBArray.getJSONObject(i).getString(PICTURE_URL));
                if (auctionDBArray.getJSONObject(i).has(PRICE))
                    auctionFromJson.setPrice(auctionDBArray.getJSONObject(i).getString(PRICE));
                if (auctionDBArray.getJSONObject(i).has(BID))
                    auctionFromJson.setBid(auctionDBArray.getJSONObject(i).getString(BID));
                if (auctionDBArray.getJSONObject(i).has(CURRENCY))
                    auctionFromJson.setCurrency(auctionDBArray.getJSONObject(i).getString(CURRENCY));
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

    public static boolean auctionExist(String id, JSONArray db){

        boolean exist = false;

        for (int i = 0; i < db.length(); i++){
            try {
                if(db.getJSONObject(i).getString(ITEM_ID).equals(id))
                    exist = true;
            }catch (JSONException e){
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
        if (price.equals(null)) {
            this.price = "-";
        }else {
            this.price = price;
        }
    }

    public String getBid() {
        return bid;
    }

    public void setBid(String bid) {
        if (bid.equals(null)) {
            this.bid = "-";
        }else {
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
        this.pictureUrl = pictureUrl;
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

    public Auction(Context context) {

    }


}
