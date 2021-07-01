package com.example.liftandpay_passenger.overpass;

import java.util.List;

public class model {

    private String type;
    private long id;
    private double lat;
    private double lon;
    private String version;
    private String name;

    private List<overpassModel> elements;

    public String getVersion() {
        return version;
    }

    public String getType() {
        return type;
    }

    public long getId() {
        return id;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public List<overpassModel> getElements() {
        return elements;
    }

    public String getName() {
        return name;
    }
}
