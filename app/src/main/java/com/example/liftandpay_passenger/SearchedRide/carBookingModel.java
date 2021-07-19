package com.example.liftandpay_passenger.SearchedRide;
import com.google.type.LatLng;

import timber.log.Timber;

public class carBookingModel {

    private String name;
    private int numberOfSeats;
    private String startLocation;
    private String endLocation;
    private String costPerKilometer;
    private String dateId;
    private String timeId;
    private String driverId;
    private double stLat;
    private double stLon;
    private double endLat;
    private double endLon;

    public carBookingModel(String name,
                           int numberOfSeats,
                           String startLocation,
                           String endLocation,
                           String costPerKilometer,
                           String dateId,
                           String timeId,
                           String driverId,
                           double stLat,
                           double stLon,
                           double endLat,
                           double endLon) {
        this.name = name;
        this.numberOfSeats = numberOfSeats;
        this.startLocation = startLocation;
        this.endLocation = endLocation;
        this.costPerKilometer = costPerKilometer;
        this.dateId = dateId;
        this.timeId = timeId;
        this.driverId = driverId;
        this.stLat = stLat;
        this.stLon = stLon;
        this.endLat = endLat;
        this.endLon = endLon;
    }


    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(String startLocation) {
        this.startLocation = startLocation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStLat(double stLat) {
        this.stLat = stLat;
    }

    public void setStLon(double stLon) {
        this.stLon = stLon;
    }

    public void setEndLat(double endLat) {
        this.endLat = endLat;
    }

    public void setEndLon(double endLon) {
        this.endLon = endLon;
    }

    public String getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(String endLocation) {
        this.endLocation = endLocation;
    }

    public String getCostPerKilometer() {
        return costPerKilometer;
    }

    public void setCostPerKilometer(String costPerKilometer) {
        this.costPerKilometer = costPerKilometer;
    }

    public String getDateId() {
        return dateId;
    }

    public void setDateId(String dateId) {
        this.dateId = dateId;
    }

    public String getTimeId() {
        return timeId;
    }

    public void setTimeId(String timeId) {
        this.timeId = timeId;
    }

    public int getNumberOfSeats() {
        return numberOfSeats;
    }

    public void setNumberOfSeats(int numberOfSeats) {
        this.numberOfSeats = numberOfSeats;
    }

    public double getStLat() {
        return stLat;
    }

    public double getStLon() {
        return stLon;
    }

    public double getEndLat() {
        return endLat;
    }

    public double getEndLon() {
        return endLon;
    }
}
