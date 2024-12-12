package com.samun.gofortrip.models;

public class Booking {
    String id , userEmail , userName , userPhone , userImageUrl , transID , placeName , placeID , placeIconUrl;
    int totalPass , totalPrice;
    boolean confirmed;

    public Booking() {}

    public Booking(String id, String userEmail, String userName, String userPhone, String userImageUrl, String transID , String placeName, String placeID, String placeIconUrl, int totalPass, int totalPrice, boolean confirmed) {
        this.id = id;
        this.userEmail = userEmail;
        this.userName = userName;
        this.userPhone = userPhone;
        this.userImageUrl = userImageUrl;
        this.placeName = placeName;
        this.placeID = placeID;
        this.placeIconUrl = placeIconUrl;
        this.totalPass = totalPass;
        this.totalPrice = totalPrice;
        this.confirmed = confirmed;
        this.transID = transID;
    }

    public String getTransID() {
        return transID;
    }

    public void setTransID(String transID) {
        this.transID = transID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getUserImageUrl() {
        return userImageUrl;
    }

    public void setUserImageUrl(String userImageUrl) {
        this.userImageUrl = userImageUrl;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getPlaceID() {
        return placeID;
    }

    public void setPlaceID(String placeID) {
        this.placeID = placeID;
    }

    public int getTotalPass() {
        return totalPass;
    }

    public void setTotalPass(int totalPass) {
        this.totalPass = totalPass;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    public String getPlaceIconUrl() {
        return placeIconUrl;
    }

    public void setPlaceIconUrl(String placeIconUrl) {
        this.placeIconUrl = placeIconUrl;
    }
}
