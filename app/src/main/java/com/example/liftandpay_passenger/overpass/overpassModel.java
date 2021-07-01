package com.example.liftandpay_passenger.overpass;

import java.util.List;

public class overpassModel {

    private String type;
    private long id;
    private double lat;
    private double lon;
    private tag tags;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public tag getTags() {
        return tags;
    }
}
