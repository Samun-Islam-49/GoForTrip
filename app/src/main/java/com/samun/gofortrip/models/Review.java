package com.samun.gofortrip.models;

public class Review {
    String id , pkgName , pkgId , userName , userEmail , userIconUrl ,  review;
    int rating;

    public Review() {
    }

    public Review(String id, String pkgName, String pkgId, String userName, String userEmail, String userIconUrl, String review, int rating) {
        this.id = id;
        this.pkgName = pkgName;
        this.pkgId = pkgId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userIconUrl = userIconUrl;
        this.review = review;
        this.rating = rating;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPkgName() {
        return pkgName;
    }

    public void setPkgName(String pkgName) {
        this.pkgName = pkgName;
    }

    public String getPkgId() {
        return pkgId;
    }

    public void setPkgId(String pkgId) {
        this.pkgId = pkgId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserIconUrl() {
        return userIconUrl;
    }

    public void setUserIconUrl(String userIconUrl) {
        this.userIconUrl = userIconUrl;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
