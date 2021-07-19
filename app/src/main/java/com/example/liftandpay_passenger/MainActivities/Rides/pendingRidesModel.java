package com.example.liftandpay_passenger.MainActivities.Rides;
public class pendingRidesModel {
    private double startLon,endLon,startLat,endLat;
    private String journey,dateTime,distance,price,status,rideId;

//data for ride history for the recycler view

    public pendingRidesModel(String journey, String dateTime, String distance, String price, double startLat, double endLat, double startLon, double endLon,String status, String rideId) {

        this.journey = journey;
        this.dateTime = dateTime;
        this.distance = distance;
        this.price =price;
        this.startLat = startLat;
        this.startLon =startLon;
        this.endLat = endLat;
        this.endLon = endLon;
        this.status = status;
        this.rideId =rideId;
    }


    public String getRideId() {
        return rideId;
    }

    public void setRideId(String rideId) {
        this.rideId = rideId;
    }

    public String getJourney() {
        return journey;
    }

    public void setJourney(String journey) {
        this.journey = journey;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDesc(String desc) {
        this.dateTime = desc;
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

    public double getStartLon() {
        return startLon;
    }

    public void setStartLon(long startLon) {
        this.startLon = startLon;
    }

    public double getEndLon() {
        return endLon;
    }

    public void setEndLon(long endLon) {
        this.endLon = endLon;
    }

    public double getStartLat() {
        return startLat;
    }

    public void setStartLat(long startLat) {
        this.startLat = startLat;
    }

    public double getEndLat() {
        return endLat;
    }

    public void setEndLat(long endLat) {
        this.endLat = endLat;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
