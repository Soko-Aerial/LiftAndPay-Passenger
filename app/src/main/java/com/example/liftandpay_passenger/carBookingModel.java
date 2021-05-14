package com.example.liftandpay_passenger;
import timber.log.Timber;

public class carBookingModel {

    public int image;
    private String startLocation;
    private String endLocation;
    private String costPerKilometer;
    private String dateId;
    private String timeId;
    private String driverId;

    public carBookingModel(int image, String startLocation, String endLocation, String costPerKilometer, String dateId, String timeId,String driverId) {
        this.image = image;
        this.startLocation = startLocation;
        this.endLocation = endLocation;
        this.costPerKilometer = costPerKilometer;
        this.dateId = dateId;
        this.timeId = timeId;
        this.driverId =driverId;

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

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
