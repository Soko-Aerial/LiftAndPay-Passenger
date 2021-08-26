package com.example.liftandpay_passenger.nominatim;

public class nominatim_model {

    private String type;
    private long id;
    private double lat;
    private double lon;
    private String display_name;

    public nominatim_model() {
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

    public String getDisplayName() {
        return display_name;
    }
}
