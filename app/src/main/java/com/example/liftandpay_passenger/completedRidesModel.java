package com.example.liftandpay_passenger;
public class completedRidesModel {
    int image;
    String header,desc,distance,price;
//data for ride history for the recycler view

    public completedRidesModel(int image, String header, String desc, String distance, String price) {
        this.image = image;
        this.header = header;
        this.desc = desc;
        this.distance = distance;
        this.price =price;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

}
