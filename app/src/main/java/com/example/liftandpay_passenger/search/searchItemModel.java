package com.example.liftandpay_passenger.search;

public class searchItemModel {

    private String theName;
    private String theAddress;
    private double lon;
    private double lat;

    public searchItemModel(String theName, String theAddress, double lon, double lat) {
        this.theName = theName;
        this.theAddress = theAddress;
        this.lon = lon;
        this.lat = lat;
    }


    public String getTheName() {
        return theName;
    }

    public void setTheName(String theName) {
        this.theName = theName;
    }

    public String getTheAddress() {
        return theAddress;
    }

    public void setTheAddress(String theAddress) {
        this.theAddress = theAddress;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }
}
