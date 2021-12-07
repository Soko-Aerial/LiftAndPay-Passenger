package com.example.liftandpay_passenger.DriversRatings;

public class RatingModel {

    private String passengerName;
    private String reviewText;
    private String passengerId;

    public RatingModel(String passengerName, String reviewText, String passengerId) {
        this.passengerName = passengerName;
        this.reviewText = reviewText;
        this.passengerId = passengerId;
    }

    public String getPassengerName() {
        return passengerName;
    }

    public void setPassengerName(String passengerName) {
        this.passengerName = passengerName;
    }

    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    public String getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(String passengerId) {
        this.passengerId = passengerId;
    }
}
