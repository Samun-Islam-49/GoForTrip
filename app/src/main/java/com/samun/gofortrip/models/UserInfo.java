package com.samun.gofortrip.models;

public class UserInfo {
    String imgUrl, name, email, phone;


    public UserInfo(String imgUrl, String name, String email, String phone) {
        this.imgUrl = imgUrl;
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    public UserInfo() {

    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
