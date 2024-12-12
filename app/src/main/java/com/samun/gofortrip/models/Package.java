package com.samun.gofortrip.models;

import java.util.List;

public class Package {
    String id , iconUrl , name , address , time , location , desc;
    List<String> imageUrls;
    int price , seat , bookedSeats;

    public Package() {}

    public Package(String id, String iconUrl, String name, String address, String time, String location , int price, int seat, List<String> imageUrls, String desc) {
        this.id = id;
        this.iconUrl = iconUrl;
        this.name = name;
        this.address = address;
        this.time = time;
        this.desc = desc;
        this.imageUrls = imageUrls;
        this.price = price;
        this.seat = seat;
        this.location = location;
    }

    public Package(String id, String iconUrl, String name, String address, String time, String location, String desc, List<String> imageUrls, int price, int seat, int bookedSeats) {
        this.id = id;
        this.iconUrl = iconUrl;
        this.name = name;
        this.address = address;
        this.time = time;
        this.location = location;
        this.desc = desc;
        this.imageUrls = imageUrls;
        this.price = price;
        this.seat = seat;
        this.bookedSeats = bookedSeats;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getSeat() {
        return seat;
    }

    public void setSeat(int seat) {
        this.seat = seat;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getBookedSeats() {
        return bookedSeats;
    }

    public void setBookedSeats(int bookedSeats) {
        this.bookedSeats = bookedSeats;
    }
}
