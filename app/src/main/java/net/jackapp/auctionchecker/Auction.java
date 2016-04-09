package net.jackapp.auctionchecker;

import android.content.Context;
import android.util.Log;

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

    public Auction() {
        Log.d("jk", "Auction object created");
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
